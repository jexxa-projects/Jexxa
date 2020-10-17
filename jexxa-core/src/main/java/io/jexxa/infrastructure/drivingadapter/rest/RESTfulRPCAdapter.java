package io.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.dsl.DocumentedContent;
import io.javalin.plugin.openapi.dsl.OpenApiBuilder;
import io.javalin.plugin.openapi.dsl.OpenApiDocumentation;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.openapi.BadRequestResponse;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
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
    public static final String OPEN_API_PATH = "io.jexxa.rest.open_api_path";

    private static final Gson GSON = new GsonBuilder().create();

    private final Properties properties;
    private Javalin javalin;
    private Server server;
    private ServerConnector sslConnector;
    private ServerConnector httpConnector;
    private OpenApiOptions openApiOptions;

    public RESTfulRPCAdapter(Properties properties)
    {
        this.properties = properties;

        Validate.isTrue(isHTTPEnabled() || isHTTPSEnabled(), "Neither HTTP (" + HTTP_PORT_PROPERTY + ") nor HTTPS (" + HTTPS_PORT_PROPERTY + ") is enabled!");

        if ( isHTTPSEnabled() )
        {
            Validate.isTrue( properties.containsKey( KEYSTORE ), "You need to define a location for keystore ("+ KEYSTORE+ ")");
            Validate.isTrue( properties.containsKey( KEYSTORE_PASSWORD ) , "You need to define a location for keystore-password ("+ KEYSTORE_PASSWORD+ ")");
        }

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
        return properties.getProperty(KEYSTORE_PASSWORD, "");
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
        //TODO: Check if this method should be refactored using IOSP
        var methodList = new RESTfulRPCConvention(object).getGETCommands();

        methodList.forEach(element -> javalin.get(element.getResourcePath(),
                ctx -> {
                    System.out.println("EXECUTE GET Method : " + element.getResourcePath());

                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    Object result = IDrivingAdapter
                            .acquireLock()
                            .invoke(element.getMethod(), object, methodParameters);

                    //TODO: Replace wildcard with correct/limited value
                    ctx.header("Access-Control-Allow-Origin", "*");
                    ctx.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
                    ctx.json(result);
                }));

        //TODO: move to separate method
        if (openApiOptions != null)
        {
            methodList.forEach( element -> {
                OpenApiDocumentation openApiDocumentation = OpenApiBuilder
                        .document()
                        .operation( openApiOperation -> {
                            openApiOperation.operationId(element.getMethod().getName());
                        })
                        .json("200", element.getMethod().getReturnType());

                openApiOptions.setDocumentation(element.getResourcePath(), HttpMethod.GET, openApiDocumentation);
            });
        }
    }

    private void registerPOSTMethods(Object object)
    {
        //TODO: Check if this method should be refactored using IOSP
        var methodList = new RESTfulRPCConvention(object).getPOSTCommands();

        methodList.forEach(element -> javalin.post(element.getResourcePath(),
                ctx -> {
                    System.out.println("EXECUTE POST Method : " + element.getResourcePath());

                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    Object result = IDrivingAdapter
                            .acquireLock()
                            .invoke(element.getMethod(), object, methodParameters);

                    if (result != null)
                    {
                        ctx.json(result);
                    }

                    //TODO: Replace wildcard with correct/limited value
                    ctx.header("Access-Control-Allow-Origin", "*");
                    ctx.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
                }));

        //TODO: move to separate method
        if (openApiOptions != null)
        {
            //Method with attributes > 1
             methodList.stream().filter(element -> element.getMethod().getParameterCount() > 1).forEach( element -> {
                var arglist = new ArrayList<DocumentedContent>();
                var schema = new Schema<>();
                schema.setName("My Schema");
                schema.setDescription("My Description: ");
                Object[] test = {LocalTime.now(), 5};
                schema.setExample(test);
                arglist.add(new DocumentedContent(schema, "application/json"));

                //arglist.add(new DocumentedContent(Integer.class));

                OpenApiDocumentation openApiDocumentation = OpenApiBuilder
                        .document()
                        .operation( openApiOperation -> {
                            openApiOperation.operationId(element.getMethod().getName());
                        })

                        //.body(arglist);
                        //.body(Integer.class);
                        .body(arglist);


                if (element.getMethod().getReturnType() != void.class )
                {
                    openApiDocumentation.json("200", element.getMethod().getReturnType());
                } else
                {
                    openApiDocumentation.result("200");
                }
                openApiOptions.setDocumentation(element.getResourcePath(), HttpMethod.POST, openApiDocumentation);

             });


            //Method with attributes <= 1
            methodList.stream().filter(element -> element.getMethod().getParameterCount() <= 1).forEach( element -> {
                OpenApiDocumentation openApiDocumentation = OpenApiBuilder
                        .document()
                        .operation( openApiOperation -> {
                            openApiOperation.operationId(element.getMethod().getName());
                        });

                if (element.getMethod().getParameters().length == 1 )
                {
                    openApiDocumentation.body(element.getMethod().getParameters()[0].getType());
                }

                if (element.getMethod().getReturnType() != void.class )
                {
                    openApiDocumentation.json("200", element.getMethod().getReturnType());
                } else
                {
                    openApiDocumentation.result("200");
                }

                openApiOptions.setDocumentation(element.getResourcePath(), HttpMethod.POST, openApiDocumentation);
            });
        }
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
        JavalinJson.setFromJsonMapper(GSON::fromJson);
        JavalinJson.setToJsonMapper(GSON::toJson);

        this.javalin = Javalin.create(config ->
                {
                    config.server(this::getServer);
                    config.showJavalinBanner = false;

                    // TODO: Code cleanup
                    if (properties.containsKey(OPEN_API_PATH))
                    {
                        config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
                        config.enableCorsForAllOrigins();
                    }
                }
        );
    }

    private OpenApiOptions getOpenApiOptions() {
        //TODO: Make it configurable via properties
        Info applicationInfo = new Info()
                .version("1.0")
                .description(properties.getProperty("io.jexxa.context.name", "Unknown Context"))
                .title(properties.getProperty("io.jexxa.context.name", "Unknown Context"));

        openApiOptions = new OpenApiOptions(applicationInfo)
                .path("/" + properties.getProperty(OPEN_API_PATH))
                .defaultDocumentation(doc ->    {
                    doc.json("400", BadRequestResponse.class);
                });

        return openApiOptions;
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

    private SslContextFactory getSslContextFactory() {
        var sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setKeyStorePath(RESTfulRPCAdapter.class.getResource("/"+ getKeystore() ).toExternalForm());
        sslContextFactory.setKeyStorePassword(getKeystorePassword());
        return sslContextFactory;
    }
}