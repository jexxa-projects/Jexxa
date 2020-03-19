package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.gson.Gson;
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

        methodList.forEach( element -> javalin.get(element.getRestURL(),
                                                 ctx -> ctx.json(element.getMethod().invoke(object))));
    }

    private void registerPOSTMethods(Object object)
    {
        var methodList = new RESTfulHTTPGenerator(object).getPOSTCommands();

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
        javalin.start(hostname, port);
    }

    @Override
    public void stop()
    {
        javalin.stop();
    }
}
