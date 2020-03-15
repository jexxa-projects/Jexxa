package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import com.google.gson.Gson;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.stereotype.infrastructure.DrivingAdapter;
import io.javalin.Javalin;


@DrivingAdapter
public class JavalinAdapter implements IDrivingAdapter
{
    private Javalin javalin = Javalin.create();

    public void register(Object object)
    {
        registerGETMethods(object);
        registerPOSTMethods(object);
    }

    private void registerGETMethods(Object object)
    {
        var methodList = new RESTfulHTTPGenerator(object).getGETCommands();

        methodList.forEach( element -> javalin.get(element.getRestURL(),
                                                 ctx -> ctx.json(element.getMethod().invoke(object))));
    }

    private void registerPOSTMethods(Object object)
    {
        var methodList = new RESTfulHTTPGenerator(object).getGETCommands();

        methodList.forEach( element -> javalin.post(element.getRestURL(), ctx -> {
                    String body = ctx.body();
                    Class<?>[] parameterTypes = element.getMethod().getParameterTypes();
                    Class<?> param1 = parameterTypes[0];
                    Object paramObject = new Gson().fromJson(body, param1);
                    element.getMethod().invoke( object, paramObject);
                }));
    }

    @Override
    public void start()
    {
        javalin.start(7000);
    }

    @Override
    public void stop()
    {
        javalin.stop();
    }
}
