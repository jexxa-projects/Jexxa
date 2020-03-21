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
public class JavalinAdapter implements IDrivingAdapter
{
    private Javalin javalin = Javalin.create();
    private String hostname;
    private int port;

    public JavalinAdapter(String hostname, int port)
    {
        checkNotNull(hostname);
        this.hostname = hostname;
        this.port = port;
    }

    public JavalinAdapter(int port)
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
        var methodList = new RESTfulHTTPGenerator(object).getGETCommands();

        methodList.forEach( element -> javalin.get(element.getResourcePath(),
                                                 ctx -> ctx.json(element.getMethod().invoke(object))));
    }

    private void registerPOSTMethods(Object object)
    {
        var methodList = new RESTfulHTTPGenerator(object).getPOSTCommands();

        methodList.forEach( element -> javalin.post(element.getResourcePath(),
                ctx -> {
                    String htmlBody = ctx.body();

                    Object[] methodParameters = deserializeParameters(htmlBody, element.getMethod());

                    element.getMethod().invoke( object, methodParameters);
                }));
    }

    private Object[] deserializeParameters(String jsonString, Method method) {
        Gson gson = new Gson();

        JsonElement jsonElement = JsonParser.parseString(jsonString);

        if (jsonElement.isJsonArray()) {
            return readArray(jsonElement.getAsJsonArray(), method);
        } else
        {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] paramArray = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; ++i)
            {
                paramArray[i] = gson.fromJson(jsonString, parameterTypes[i]);
            }

            return paramArray;
        }
    }

    private Object[] readArray(JsonArray jsonArray, Method method)
    {
        if (jsonArray.size() != method.getParameterTypes().length)
        {
            throw new IllegalArgumentException("Invalid Number of parameters");
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
