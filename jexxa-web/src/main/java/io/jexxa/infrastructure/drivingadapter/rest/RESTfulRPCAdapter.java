package io.jexxa.infrastructure.drivingadapter.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.json.JsonMapper;
import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.infrastructure.drivingadapter.rest.openapi.OpenAPIConvention;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.json.JSONConverter;
import io.jexxa.utils.json.JSONManager;
import io.jexxa.utils.properties.Secret;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCConvention.createRPCConvention;


public class RESTfulRPCAdapter implements IDrivingAdapter
{


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

        validateIsTrue(isHTTPEnabled() || isHTTPSEnabled(), "Neither HTTP (" + JexxaWebProperties.JEXXA_REST_PORT + ") nor HTTPS (" + JexxaWebProperties.JEXXA_REST_HTTPS_PORT + ") is enabled!");

        if ( isHTTPSEnabled() )
        {
            validateIsTrue( properties.containsKey( JexxaWebProperties.JEXXA_REST_KEYSTORE), "You need to define a location for keystore ("+ JexxaWebProperties.JEXXA_REST_KEYSTORE + ")");
            validateIsTrue( properties.containsKey( JexxaWebProperties.JEXXA_REST_KEYSTORE_PASSWORD) || properties.containsKey( JexxaWebProperties.JEXXA_REST_FILE_KEYSTORE_PASSWORD)
                    , "You need to define a location for keystore-password ("+ JexxaWebProperties.JEXXA_REST_KEYSTORE_PASSWORD + "or" + JexxaWebProperties.JEXXA_REST_FILE_KEYSTORE_PASSWORD + ")");
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
            if (e.getMessage().contains("Port already in use.")) // Javalin states its default port of the server. Therefore, we correct the error message here."
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
        return properties.containsKey(JexxaWebProperties.JEXXA_REST_PORT);
    }

    boolean isHTTPSEnabled()
    {
        return properties.containsKey(JexxaWebProperties.JEXXA_REST_HTTPS_PORT);
    }

    String getHostname()
    {
        return properties.getProperty(JexxaWebProperties.JEXXA_REST_HOST, "0.0.0.0");
    }

    String getKeystore()
    {
        return properties.getProperty(JexxaWebProperties.JEXXA_REST_KEYSTORE, "");
    }

    String getKeystorePassword()
    {
        return new Secret(properties, JexxaWebProperties.JEXXA_REST_KEYSTORE_PASSWORD, JexxaWebProperties.JEXXA_REST_FILE_KEYSTORE_PASSWORD)
                .getSecret();
    }


    private int getHTTPPortFromProperties()
    {
        return Integer.parseInt(properties.getProperty(JexxaWebProperties.JEXXA_REST_PORT, "0"));
    }

    private int getHTTPSPortFromProperties()
    {
        return Integer.parseInt(properties.getProperty(JexxaWebProperties.JEXXA_REST_HTTPS_PORT, "0"));
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
        javalin.exception(InvocationTargetException.class, (e, ctx) ->  handleTargetException(e.getTargetException(), ctx));
        javalin.exception(InvocationTargetRuntimeException.class, (e, ctx) ->  handleTargetException(e.getTargetException(), ctx));
    }

    private void handleTargetException(Throwable targetException, Context ctx )
    {
        if ( targetException != null )
        {
            targetException.getStackTrace(); // Ensures that stack trace is filled in
            var ctxMethod = ctx.method();
            var ctxBody = ctx.body();
            var ctxPath = ctx.path();

            JexxaLogger.getLogger(RESTfulRPCAdapter.class).error("{} occurred when processing {} request {}",
                    targetException.getClass().getSimpleName(), ctxMethod, ctxPath);
            JexxaLogger.getLogger(RESTfulRPCAdapter.class).error("Content of Body: {}", ctxBody);
            JexxaLogger.getLogger(RESTfulRPCAdapter.class).error("Exception message: {}", targetException.getMessage());

            var exceptionWrapper = new JsonObject();
            exceptionWrapper.addProperty("ExceptionType", targetException.getClass().getName());
            exceptionWrapper.addProperty("Exception", jsonConverter.toJson(targetException));
            exceptionWrapper.addProperty("ApplicationType", jsonConverter.toJson("application/json"));

            ctx.result(exceptionWrapper.toString());
        }
        ctx.status(400);
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
        var invocationHandler = InvocationManager.getInvocationHandler(object);


        var result = Optional.ofNullable(
                invocationHandler.invoke(method.getMethod(), object, methodParameters)
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

        if ( properties.getProperty(JexxaWebProperties.JEXXA_REST_STATIC_FILES_EXTERNAL, "false").equalsIgnoreCase("true") )
        {
            location = Location.EXTERNAL;
        }

        if ( properties.containsKey(JexxaWebProperties.JEXXA_REST_STATIC_FILES_ROOT) )
        {
            javalinConfig.addStaticFiles(properties.getProperty(JexxaWebProperties.JEXXA_REST_STATIC_FILES_ROOT), location );
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
        URL keystoreURL = RESTfulRPCAdapter.class.getResource("/" + getKeystore());

        if ( keystoreURL == null )
        {
            File file = new File(getKeystore());
            if(file.exists() && !file.isDirectory())
            {
                try
                {
                    keystoreURL =file.toURI().toURL();
                } catch (MalformedURLException e)
                {
                    throw new IllegalArgumentException(e);
                }
            } else
            {
                throw new IllegalArgumentException("File Keystore " + getKeystore() + " is not available! Please check the setting " + JexxaWebProperties.JEXXA_REST_KEYSTORE);
            }
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