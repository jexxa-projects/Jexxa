package io.jexxa.infrastructure.drivingadapter.rest;

import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCConvention.createRPCConvention;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.json.JsonMapper;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.openapi.OpenAPIConvention;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.json.JSONConverter;
import io.jexxa.utils.json.JSONManager;
import io.jexxa.utils.properties.Secret;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jetbrains.annotations.NotNull;


public class RESTfulRPCAdapter implements IDrivingAdapter
{
    public static final String HOST_PROPERTY = "io.jexxa.rest.host";
    public static final String HTTP_PORT_PROPERTY = "io.jexxa.rest.port";
    public static final String HTTPS_PORT_PROPERTY = "io.jexxa.rest.https_port";
    public static final String KEYSTORE = "io.jexxa.rest.keystore";
    public static final String KEYSTORE_PASSWORD = "io.jexxa.rest.keystore_password";
    public static final String KEYSTORE_PASSWORD_FILE = "io.jexxa.rest.file.keystore_password";
    public static final String OPEN_API_PATH = "io.jexxa.rest.open_api_path";
    public static final String STATIC_FILES_ROOT = "io.jexxa.rest.static_files_root";
    public static final String STATIC_FILES_EXTERNAL = "io.jexxa.rest.static_files_external";


    private final JSONConverter jsonConverter = JSONManager.getJSONConverter();
    private final Properties properties;
    private Javalin javalin;
    private Server server;
    private ServerConnector sslConnector;
    private ServerConnector httpConnector;
    private OpenAPIConvention openAPIConvention;

    private static final Map<Properties, RESTfulRPCAdapter> RPC_ADAPTER_MAP = new HashMap<>();

    private RESTfulRPCAdapter(Properties properties)
    {
        this.properties = properties;

        validateIsTrue(isHTTPEnabled() || isHTTPSEnabled(), "Neither HTTP (" + HTTP_PORT_PROPERTY + ") nor HTTPS (" + HTTPS_PORT_PROPERTY + ") is enabled!");

        if ( isHTTPSEnabled() )
        {
            validateIsTrue( properties.containsKey( KEYSTORE ), "You need to define a location for keystore ("+ KEYSTORE+ ")");
            validateIsTrue( properties.containsKey( KEYSTORE_PASSWORD ) || properties.containsKey( KEYSTORE_PASSWORD_FILE )
                    , "You need to define a location for keystore-password ("+ KEYSTORE_PASSWORD+ "or" + KEYSTORE_PASSWORD_FILE+ ")");
        }

        setupJavalin();

        registerExceptionHandler();
    }

    public static RESTfulRPCAdapter createAdapter(Properties properties)
    {
        if ( RPC_ADAPTER_MAP.containsKey(properties) )
        {
            JexxaLogger.getLogger(RESTfulRPCAdapter.class).warn("Tried to create an RESTfulRPCAdapter with same properties twice! Return already instantiated adapter.");
        } else {
            RPC_ADAPTER_MAP.put(properties, new RESTfulRPCAdapter(properties));
        }

        return RPC_ADAPTER_MAP.get(properties);
    }

    public void register(Object object)
    {
        Objects.requireNonNull(object);
        registerGETMethods(object);
        registerPOSTMethods(object);
    }


    @Override
    public void start()
    {
        try
        {
            javalin.start();

            if (httpConnector != null ) {
                openAPIConvention.getPath().ifPresent(path -> JexxaLogger.getLogger(this.getClass()).info("OpenAPI documentation available at: {}"
                        , "http://" + httpConnector.getHost() + ":" + httpConnector.getPort() +  path ) );
            }
            if (sslConnector != null ) {
                openAPIConvention.getPath().ifPresent(path -> JexxaLogger.getLogger(this.getClass()).info("OpenAPI documentation available at: {}"
                        , "https://" + sslConnector.getHost() + ":" + sslConnector.getPort() + path ) );
            }
        } catch (RuntimeException e)
        {
            if (e.getMessage().contains("Port already in use.")) // Javalin states its default port of the server. Therefore, we correct the error message here"
            {
                throw new IllegalStateException(
                        RESTfulRPCAdapter.class.getSimpleName()
                        + ": "
                        + e.getCause().getMessage()
                        + ". Please check that IP address is correct and port is not in use.", e
                );
            }
            throw e;
        }
    }

    @Override
    public void stop()
    {
        RPC_ADAPTER_MAP.remove(properties);

        javalin.stop();
        Optional.ofNullable(httpConnector).ifPresent(ServerConnector::close);
        Optional.ofNullable(sslConnector).ifPresent(ServerConnector::close);
    }

    @SuppressWarnings("unused")
    public int getHTTPSPort()
    {
        if (sslConnector != null)
        {
            return sslConnector.getLocalPort();
        }

        return getHTTPSPortFromProperties();
    }

    public int getHTTPPort()
    {
        if (httpConnector != null)
        {
            return httpConnector.getLocalPort();
        }

        return getHTTPPortFromProperties();
    }

    boolean isHTTPEnabled()
    {
        return properties.containsKey(HTTP_PORT_PROPERTY);
    }

    boolean isHTTPSEnabled()
    {
        return properties.containsKey(HTTPS_PORT_PROPERTY);
    }

    String getHostname()
    {
        return properties.getProperty(HOST_PROPERTY, "0.0.0.0");
    }

    String getKeystore()
    {
        return properties.getProperty(KEYSTORE, "");
    }

    String getKeystorePassword()
    {
        return new Secret(properties, KEYSTORE_PASSWORD, KEYSTORE_PASSWORD_FILE)
                .getSecret();
    }


    private int getHTTPPortFromProperties()
    {
        return Integer.parseInt(properties.getProperty(HTTP_PORT_PROPERTY, "0"));
    }

    private int getHTTPSPortFromProperties()
    {
        return Integer.parseInt(properties.getProperty(HTTPS_PORT_PROPERTY, "0"));
    }

    /**
     * Mapping of exception is done as follows
     * <pre>
     * {@code
     *   {
     *     "Exception": "<exception as json>",
     *     "ExceptionType": "<Type of the exception>",
     *     "ApplicationType": "application/json"
     *   }
     * }
     * </pre>
     *
     */
    private void registerExceptionHandler()
    {
        //Exception Handler for thrown Exception from methods
        javalin.exception(InvocationTargetException.class, (e, ctx) -> {
            var targetException = e.getTargetException();
            if ( targetException != null )
            {
                targetException.getStackTrace(); // Ensures that stack trace is filled in

                JexxaLogger.getLogger(RESTfulRPCAdapter.class).error("{} occurred when processing {} request {}",
                        targetException.getClass().getSimpleName(), ctx.method(), ctx.path());
                JexxaLogger.getLogger(RESTfulRPCAdapter.class).error("Content of Body: {}", ctx.body());
                JexxaLogger.getLogger(RESTfulRPCAdapter.class).error("Exception message: {}", targetException.getMessage());

                var exceptionWrapper = new JsonObject();
                exceptionWrapper.addProperty("ExceptionType", targetException.getClass().getName());
                exceptionWrapper.addProperty("Exception", jsonConverter.toJson(targetException));
                exceptionWrapper.addProperty("ApplicationType", jsonConverter.toJson("application/json"));

                ctx.result(exceptionWrapper.toString());
            }
            ctx.status(400);
        });
    }

    private void registerGETMethods(Object object)
    {
        var getCommands = createRPCConvention(object).getGETCommands();

        getCommands.forEach(
                method -> javalin.get(
                        method.getResourcePath(),
                        httpCtx -> invokeMethod(object, method, httpCtx)
                )
        );

        getCommands.forEach( method -> openAPIConvention.documentGET(method.getMethod(), method.getResourcePath()));
    }

    private void registerPOSTMethods(Object object)
    {
        var postCommands = createRPCConvention(object).getPOSTCommands();

        postCommands.forEach(
                method -> javalin.post(
                        method.getResourcePath(),
                        httpCtx -> invokeMethod(object, method, httpCtx)
                )
        );

        postCommands.forEach( method -> openAPIConvention.documentPOST(method.getMethod(), method.getResourcePath()));
    }



    private void invokeMethod(Object object, RESTfulRPCConvention.RESTfulRPCMethod method, Context httpContext ) throws InvocationTargetException, IllegalAccessException
    {
        Object[] methodParameters = deserializeParameters(httpContext.body(), method.getMethod());

        var result = Optional.ofNullable(
                IDrivingAdapter
                        .acquireLock()
                        .invoke(method.getMethod(), object, methodParameters)
        );

        //At the moment we do not handle any credentials
        httpContext.header("Access-Control-Allow-Origin", "*");
        httpContext.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

        result.ifPresent(httpContext::json);
    }

    private Object[] deserializeParameters(String jsonString, Method method)
    {
        if (jsonString == null ||
                jsonString.isEmpty() ||
                method.getParameterCount() == 0)
        {
            return new Object[]{};
        }

        var jsonElement = JsonParser.parseString(jsonString);

        // In case we have more than one attribute, we assume a JSonArray
        if ( method.getParameterCount() > 1)
        {
            if ( !jsonElement.isJsonArray() )
            {
                throw new IllegalArgumentException("Multiple method attributes musst be passed inside a JSonArray");
            }
            return readArray(jsonElement.getAsJsonArray(), method);
        }
        else
        {
            var result = new Object[1];
            result[0] = jsonConverter.fromJson(jsonString, method.getParameterTypes()[0]);
            return result;
        }
    }

    private Object[] readArray(JsonArray jsonArray, Method method)
    {
        if (jsonArray.size() != method.getParameterCount())
        {
            throw new IllegalArgumentException("Invalid Number of parameters for method " + method.getName());
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        var paramArray = new Object[parameterTypes.length];

        for (var i = 0; i < parameterTypes.length; ++i)
        {
            paramArray[i] = jsonConverter.fromJson(jsonArray.get(i).toString(), parameterTypes[i]);
        }

        return paramArray;
    }

    private void setupJavalin()
    {
        this.javalin = Javalin.create(this::getJavalinConfig);
    }

    private void getJavalinConfig(JavalinConfig javalinConfig)
    {
        javalinConfig.server(this::getServer);
        javalinConfig.showJavalinBanner = false;
        javalinConfig.jsonMapper(new JexxaJSONMapper());
        Location location = Location.CLASSPATH;

        if ( properties.getProperty(STATIC_FILES_EXTERNAL, "false").equalsIgnoreCase("true") )
        {
            location = Location.EXTERNAL;
        }

        if ( properties.containsKey(STATIC_FILES_ROOT) )
        {
            javalinConfig.addStaticFiles(properties.getProperty(STATIC_FILES_ROOT), location );
        }

        this.openAPIConvention = new OpenAPIConvention(properties, javalinConfig );
    }

    private Server getServer()
    {
        if ( server == null )
        {
            server = new Server();
            if (isHTTPEnabled())
            {
                httpConnector = new ServerConnector(server);
                httpConnector.setHost(getHostname());
                httpConnector.setPort(getHTTPPortFromProperties());
                server.addConnector(httpConnector);
            }

            if (isHTTPSEnabled())
            {
                sslConnector = new ServerConnector(server, getSslContextFactory());
                sslConnector.setHost(getHostname());
                sslConnector.setPort(getHTTPSPortFromProperties());
                server.addConnector(sslConnector);
            }
        }

        return server;
    }

    private SslContextFactory getSslContextFactory()
    {
        var keystoreURL = RESTfulRPCAdapter.class.getResource("/" + getKeystore());
        if ( keystoreURL == null )
        {
            throw new IllegalArgumentException("Keystore " + getKeystore() + " is not available! Please check the setting " + KEYSTORE);
        }

        var sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(keystoreURL.toExternalForm());
        sslContextFactory.setKeyStorePassword(getKeystorePassword());
        return sslContextFactory;
    }

    void validateIsTrue( boolean expression, String message)
    {
        if (!expression)
        {
            throw new IllegalArgumentException(message);
        }
    }

    private static class JexxaJSONMapper implements JsonMapper
    {
        @NotNull
        @Override
        public String toJsonString(@NotNull Object obj) {
            return JSONManager.getJSONConverter().toJson(obj);
        }

        @NotNull
        @Override
        public  <T> T fromJsonString(@NotNull String json, @NotNull Class<T> targetClass) {
            return JSONManager.getJSONConverter().fromJson(json, targetClass);
        }

    }

}