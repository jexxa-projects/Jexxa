package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.util.List;

import com.google.gson.Gson;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.stereotype.infrastructure.DrivingAdapter;
import io.javalin.Javalin;


@DrivingAdapter
public class JavalinREST implements IDrivingAdapter
{
    private Javalin app;
    

    public void register(Object object)
    {
        app = Javalin.create();

        List<RESTPfade.RestURL> allMethods = RESTPfade.getRestURLs(object.getClass());

        allMethods.forEach( element -> {
            if (!(element.getMethod().getReturnType().isInstance(void.class)))
            {
                app.get(element.getRestURL(), ctx -> ctx.json(element.getMethod().invoke( object)));
            }
            else
            {
                app.post(element.getRestURL(), ctx -> {
                    String body = ctx.body();
                    Class<?>[] parameterTypes = element.getMethod().getParameterTypes();
                    Class<?> param1 = parameterTypes[0];
                    Object paramObject = new Gson().fromJson(body, param1);
                    element.getMethod().invoke( object, paramObject);
                });
            }
        });

    }

    @Override
    public void start()
    {
        app.start(7000);
    }

    @Override
    public void stop()
    {
        app = null;         
    }
}
