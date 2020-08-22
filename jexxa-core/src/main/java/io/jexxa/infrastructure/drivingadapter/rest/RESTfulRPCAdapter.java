package io.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang3.Validate;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;


public class RESTfulRPCAdapter implements IDrivingAdapter
{
    public static final String HOST_PROPERTY = "io.jexxa.rest.host";
    public static final String HTTP_PORT_PROPERTY = "io.jexxa.rest.port";
    public static final String HTTPS_PORT_PROPERTY = "io.jexxa.rest.https_port";
    public static final String KEYSTORE = "io.jexxa.rest.keystore";
    public static final String KEYSTORE_PASSWORD = "io.jexxa.rest.keystore_password";

    private Javalin javalin;
    private String hostname;
    private int port;

    private int httpsPort;
    private String keyStore;
    private String keyStorePassword;

    private final Properties properties;
    
    public RESTfulRPCAdapter(final Properties properties)
    {
        this.properties = properties;

        readProperties(properties);

        Validate.notNull(hostname);
        Validate.isTrue(port >= 0);
        
        setupJavalin();

        registerExceptionHandler();
    }

    public void register(Object object)
    {
        Validate.notNull(object);
        registerGETMethods(object);
        registerPOSTMethods(object);
    }


    @Override
    public void start()
    {
        javalin.start();
    }
    
    @Override
    public void stop()
    {
        javalin.stop();
    }

    public int getPort()
    {
        return javalin.port();
    }

    /**
     * Mapping of exception is done as follows
     * <pre>
     * {@code
     *   {
     *     "Exception": "<exception as json>",
     *     "ExceptionType": "<Type of the exception>",
     *   }
     * }
     * </pre>
     * 
     */
    private void registerExceptionHandler()
    {
        //Exception Handler for thrown Exception from methods
        javalin.exception(InvocationTargetException.class, (e, ctx) -> {
            Gson gson = new Gson();
            JsonObject exceptionWrapper = new JsonObject();
            exceptionWrapper.addProperty("ExceptionType", e.getCause().getClass().getName());
            exceptionWrapper.addProperty("Exception", gson.toJson(e));

            ctx.result(exceptionWrapper.toString());
            ctx.status(400);
        });
    }

    private void registerGETMethods(Object object)
    {
        var methodList = new RESTfulRPCConvention(object).getGETCommands();

        methodList.forEach(element -> javalin.get(element.getResourcePath(),
                ctx -> {
                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    Object result = IDrivingAdapter
                            .acquireLock()
                            .invoke(element.getMethod(), object, methodParameters);

                    ctx.json(result);
                }));
    }

    private void registerPOSTMethods(Object object)
    {
        var methodList = new RESTfulRPCConvention(object).getPOSTCommands();

        methodList.forEach(element -> javalin.post(element.getResourcePath(),
                ctx -> {
                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    Object result = IDrivingAdapter
                            .acquireLock()
                            .invoke(element.getMethod(), object, methodParameters);
                    
                    if (result != null)
                    {
                        ctx.json(result);
                    }
                }));
    }

    private Object[] deserializeParameters(String jsonString, Method method)
    {
        if (jsonString == null ||
                jsonString.isEmpty() ||
                method.getParameterCount() == 0)
        {
            return new Object[]{};
        }

        Gson gson = new Gson();
        JsonElement jsonElement = JsonParser.parseString(jsonString);

        if (jsonElement.isJsonArray())
        {
            return readArray(jsonElement.getAsJsonArray(), method);
        }
        else
        {
            Object[] result = new Object[1];
            result[0] = gson.fromJson(jsonString, method.getParameterTypes()[0]);
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
        Object[] paramArray = new Object[parameterTypes.length];

        Gson gson = new Gson();

        for (int i = 0; i < parameterTypes.length; ++i)
        {
            paramArray[i] = gson.fromJson(jsonArray.get(i), parameterTypes[i]);
        }

        return paramArray;
    }

    private void setupJavalin()
    {
        this.javalin = Javalin.create(config -> config.server(this::getServer));
        this.javalin.config.showJavalinBanner = false;
    }

    Server getServer( )
    {
        Server server = new Server();

        if ( properties.containsKey(HTTPS_PORT_PROPERTY) )
        {
            ServerConnector sslConnector = new ServerConnector(server, getSslContextFactory());
            sslConnector.setHost(hostname);
            sslConnector.setPort(httpsPort);
            server.addConnector(sslConnector);
        }

        if ( properties.containsKey(HTTP_PORT_PROPERTY) )
        {
            ServerConnector connector = new ServerConnector(server);
            connector.setHost(hostname);
            connector.setPort(port);
            server.addConnector(connector);
        }
        
        return server;
    }

    private SslContextFactory getSslContextFactory() {
        SslContextFactory sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(RESTfulRPCAdapter.class.getResource("/"+ keyStore ).toExternalForm());
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        return sslContextFactory;
    }

    private void readProperties(Properties properties)
    {
        this.hostname = properties.getProperty(HOST_PROPERTY, "0.0.0.0");
        this.port = Integer.parseInt(properties.getProperty(HTTP_PORT_PROPERTY, "0"));

        this.httpsPort = Integer.parseInt(properties.getProperty(HTTPS_PORT_PROPERTY, "0"));
        this.keyStore = properties.getProperty(KEYSTORE, "");
        this.keyStorePassword = properties.getProperty(KEYSTORE_PASSWORD, "");
    }
}