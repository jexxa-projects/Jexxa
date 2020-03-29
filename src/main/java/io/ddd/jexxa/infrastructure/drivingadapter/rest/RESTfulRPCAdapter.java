package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.javalin.Javalin;
import org.apache.commons.lang.Validate;


public class RESTfulRPCAdapter implements IDrivingAdapter
{
    public static final String HOST_PROPERTY = "io.ddd.jexxa.rest.host";
    public static final String PORT_PROPERTY = "io.ddd.jexxa.rest.port";

    private Javalin javalin = Javalin.create();
    private String hostname;
    private int port;

    public RESTfulRPCAdapter(String hostname, int port)
    {
        Validate.notNull(hostname);
        Validate.isTrue(port >= 0);

        this.hostname = hostname;
        this.port = port;

        registerExceptionHandler();
    }

    public RESTfulRPCAdapter(Properties properties)
    {
        Validate.notNull(properties.getProperty(HOST_PROPERTY));
        Validate.notNull(properties.getProperty(PORT_PROPERTY));

        this.hostname = properties.getProperty(HOST_PROPERTY);
        this.port = Integer.parseInt(properties.getProperty(PORT_PROPERTY));

        Validate.notNull(hostname);
        Validate.isTrue(port >= 0);
        registerExceptionHandler();
    }

    public void register(Object object)
    {
        Validate.notNull(object);
        registerGETMethods(object);
        registerPOSTMethods(object);
    }

    private void registerExceptionHandler()
    {
        //Exception Handler for thrown Exception from methods
        javalin.exception(InvocationTargetException.class, (e, ctx) -> {
            //Exception is stated by a Json Array ["<Exception type>", "<serialized exception>"]
            Object[] result = {e.getCause().getClass().getName(), e};
            Gson gson = new Gson();
            ctx.result(gson.toJson(result));
            ctx.status(400);
        });
    }
    
    private void registerGETMethods(Object object)
    {
        var methodList = new RESTfulRPCModel(object).getGETCommands();

        methodList.forEach(element -> javalin.get(element.getResourcePath(),
                ctx -> {
                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    ctx.json(element.getMethod().invoke(object, methodParameters));
                }));
    }

    private void registerPOSTMethods(Object object)
    {
        var methodList = new RESTfulRPCModel(object).getPOSTCommands();

        methodList.forEach( element -> javalin.post(element.getResourcePath(),
                ctx -> {
                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    Object result = element.getMethod().invoke(object, methodParameters);

                    if (result != null)
                    {
                        ctx.json(result);
                    }
                }));
    }

    private Object[] deserializeParameters(String jsonString, Method method) {
        if ( jsonString == null ||
             jsonString.isEmpty() ||
             method.getParameterCount() == 0)
        {
            return new Object[]{};
        }

        Gson gson = new Gson();
        JsonElement jsonElement = JsonParser.parseString(jsonString);

        if (jsonElement.isJsonArray()) {
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



    @Override
    public void start()
    {
        javalin.start(hostname, port);
    }

    @Override
    public void stop()
    {
        javalin.stop();
    }
}
