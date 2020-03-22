package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.jexxa.infrastructure.stereotype.DrivingAdapter;
import io.javalin.Javalin;


@DrivingAdapter
public class RESTfulRPCAdapter implements IDrivingAdapter
{
    private Javalin javalin = Javalin.create();
    private String hostname;
    private int port;

    public RESTfulRPCAdapter(String hostname, int port)
    {
        checkNotNull(hostname);
        this.hostname = hostname;
        this.port = port;
    }

    public RESTfulRPCAdapter(int port)
    {
        this("localhost", port);
    }

    public void register(Object object)
    {
        registerGETMethods(object);
        registerPOSTMethods(object);
    }

    private void registerGETMethods(Object object)
    {
        var methodList = new RESTfulRPCConvention(object).getGETCommands();

        methodList.forEach(element -> javalin.get(element.getResourcePath(),
                ctx -> {
                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    ctx.json(element.getMethod().invoke(object, methodParameters));
                }));
    }

    private void registerPOSTMethods(Object object)
    {
        var methodList = new RESTfulRPCConvention(object).getPOSTCommands();

        methodList.forEach( element -> javalin.post(element.getResourcePath(),
                ctx -> {
                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    Object result = element.getMethod().invoke( object, methodParameters);

                    if ( result != null )
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
