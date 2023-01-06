package io.jexxa.infrastructure.drivingadapter.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JsonMapper;
import io.javalin.plugin.bundled.CorsPluginConfig;
import io.javalin.util.JavalinLogger;
import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;
import io.jexxa.infrastructure.drivingadapter.rest.openapi.OpenAPIConvention;
import io.jexxa.utils.JexxaBanner;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.json.JSONConverter;
import io.jexxa.utils.json.JSONManager;
import io.jexxa.utils.properties.Secret;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static io.jexxa.infrastructure.drivingadapter.rest.JexxaWebProperties.JEXXA_REST_OPEN_API_PATH;
import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCConvention.createRPCConvention;


public final class RESTfulRPCAdapter implements IDrivingAdapter
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

        openAPIConvention = new OpenAPIConvention(properties);

        setupJavalin();

        registerExceptionHandler();

        JexxaBanner.addAccessBanner(this::bannerInformation);
    }

    public static RESTfulRPCAdapter createAdapter(Properties properties)
    {
        JavalinLogger.startupInfo = false;

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
        } catch (RuntimeException e)
        {
            if (e.getMessage().contains("Port already in use.")) // Javalin states its default port of the server. Therefore, we correct the error message here.
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
            if (sslConnector.getPort() != 0 )
            {
                return sslConnector.getPort();
            }
            return sslConnector.getLocalPort();
        }

        return getHTTPSPortFromProperties();
    }

    public int getHTTPPort()
    {
        if (httpConnector != null)
        {
            if (httpConnector.getPort() != 0 )
            {
                return httpConnector.getPort();
            }
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


    @SuppressWarnings("HttpUrlsUsage") //http is used for showing access possibilities
    public void bannerInformation(@SuppressWarnings("unused") Properties properties)
    {
        // Print Listening ports
        if (isHTTPEnabled() ) {
            JexxaLogger.getLogger(JexxaBanner.class).info("Listening on: {}", "http://" + getHostname() + ":" + getHTTPPort()  );
        }

        if (isHTTPSEnabled() ) {
            JexxaLogger.getLogger(JexxaBanner.class).info("Listening on: {}", "https://" + getHostname() + ":" + getHTTPSPort() );
        }

        // Print OPENAPI links
        if (isHTTPEnabled()) {
            openAPIConvention.getPath().ifPresent(path -> JexxaLogger.getLogger(JexxaBanner.class).info("OpenAPI available at: {}"
                    , "http://" + getHostname() + ":" + getHTTPPort() +  path ) );
        }
        if (isHTTPSEnabled()) {
            openAPIConvention.getPath().ifPresent(path -> JexxaLogger.getLogger(JexxaBanner.class).info("OpenAPI available at: {}"
                    , "https://" + getHostname() + ":" + getHTTPSPort() + path ) );
        }
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
        javalin.exception(IllegalArgumentException.class, this::handleTargetException);
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
            exceptionWrapper.addProperty("Exception", toJson(targetException));
            exceptionWrapper.addProperty("ApplicationType", jsonConverter.toJson("application/json"));

            ctx.result(exceptionWrapper.toString());
        }
        ctx.status(400);
    }

    private String toJson(Throwable e)
    {
        try {
            return jsonConverter.toJson(e);
        } catch (RuntimeException re){
            return jsonConverter.toJson(new IllegalArgumentException(e.getMessage()));
        }
    }

    private void registerGETMethods(Object object)
    {
        var getCommands = createRPCConvention(object).getGETCommands();

        getCommands.forEach(
                method -> javalin.get(
                        method.resourcePath(),
                        httpCtx -> invokeMethod(object, method, httpCtx)
                )
        );

        getCommands.forEach( method -> openAPIConvention.documentGET(method.method(), method.resourcePath()));
    }

    private void registerPOSTMethods(Object object)
    {
        var postCommands = createRPCConvention(object).getPOSTCommands();

        postCommands.forEach(
                method -> javalin.post(
                        method.resourcePath(),
                        httpCtx -> invokeMethod(object, method, httpCtx)
                )
        );

        postCommands.forEach( method -> openAPIConvention.documentPOST(method.method(), method.resourcePath()));
    }



    private void invokeMethod(Object object, RESTfulRPCConvention.RESTfulRPCMethod method, Context httpContext )
    {
        Object[] methodParameters = deserializeParameters(httpContext.body(), method.method());
        var invocationHandler = InvocationManager.getInvocationHandler(object);


        var result = Optional.ofNullable(
                invocationHandler.invoke(method.method(), object, methodParameters)
        );

        //At the moment we do not handle any credentials
        httpContext.header("Access-Control-Allow-Origin", "*");
        httpContext.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");

        result.ifPresent(httpContext::json);
    }

    private Object[] deserializeParameters(String jsonString, Method method)
    {
        try {
            if ( jsonString == null
                    || jsonString.isEmpty()
                    || method.getParameterCount() == 0)
            {
                return new Object[]{};
            }

            var jsonElement = JsonParser.parseString(jsonString);

            // In case we have more than one attribute, we assume a JSonArray
            if (method.getParameterCount() > 1) {
                return readArray(jsonElement.getAsJsonArray(), method);
            } else {
                return new Object[]{jsonConverter.fromJson(jsonString, method.getParameterTypes()[0])};
            }
        }
        catch (IllegalArgumentException e)  {
            throw e;
        }
        catch (RuntimeException e)  {
            if (e.getCause() != null && e.getCause().getMessage() != null)
            {
                throw new IllegalArgumentException("Could not deserialize attributes for method " + method.getName() + " Reason: " + e.getCause().getMessage(), e );
            } else {
                throw new IllegalArgumentException("Could not deserialize attributes for method " + method.getName() + " Reason: " + e.getMessage(), e );
            }
        }
    }

    private Object[] readArray(JsonElement jsonElement, Method method)
    {
        if (!jsonElement.isJsonArray()) {
            throw new IllegalArgumentException("Multiple method attributes musst be passed inside a JSonArray");
        }
        var jsonArray = jsonElement.getAsJsonArray();

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

        var openAPIPath = properties.getProperty(JEXXA_REST_OPEN_API_PATH);
        if (openAPIPath != null && !openAPIPath.isEmpty()) {
            openAPIConvention = new OpenAPIConvention(properties);
            javalin.get(openAPIPath, httpCtx -> httpCtx.result(openAPIConvention.getOpenAPI()));
        }

    }

    private void getJavalinConfig(JavalinConfig javalinConfig) {
        javalinConfig.jetty.server(this::getServer);

        javalinConfig.showJavalinBanner = false;
        javalinConfig.jsonMapper(new JexxaJSONMapper());
        Location location = Location.CLASSPATH;

        if (properties.getProperty(JexxaWebProperties.JEXXA_REST_STATIC_FILES_EXTERNAL, "false").equalsIgnoreCase("true")) {
            location = Location.EXTERNAL;
        }

        if (properties.containsKey(JexxaWebProperties.JEXXA_REST_STATIC_FILES_ROOT)) {
            javalinConfig.staticFiles.add(properties.getProperty(JexxaWebProperties.JEXXA_REST_STATIC_FILES_ROOT), location);
        }

        javalinConfig.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
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
                HttpConfiguration httpsConfig = new HttpConfiguration();
                httpsConfig.setSendServerVersion(false);
                httpsConfig.setRequestHeaderSize(512 * 1024);
                httpsConfig.setResponseHeaderSize(512 * 1024);

                SecureRequestCustomizer src = new SecureRequestCustomizer();
                src.setSniHostCheck(false);
                httpsConfig.addCustomizer(src);

                sslConnector = new ServerConnector(server, getSslContextFactory(), new HttpConnectionFactory(httpsConfig));
                sslConnector.setHost(getHostname());
                sslConnector.setPort(getHTTPSPortFromProperties());

                server.addConnector(sslConnector);
            }
        }

        return server;
    }

    private SslContextFactory.Server getSslContextFactory()
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
        public String toJsonString(@NotNull Object obj, @NotNull Type targetClass) {
            return JSONManager.getJSONConverter().toJson(obj);
        }

        @NotNull
        @Override
        public  <T> T fromJsonString(@NotNull String json, @NotNull Type targetClass) {
            return JSONManager.getJSONConverter().fromJson(json, targetClass);
        }
    }
}