package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.google.gson.Gson;
import io.ddd.jexxa.infrastructure.PortScanner;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.stereotype.infrastructure.DrivingAdapter;
import io.javalin.Javalin;

@DrivingAdapter
public class JavalinREST implements IDrivingAdapter
{
    private Javalin app;

    private JavalinREST()
    {

    }

    public void generate()
    {

        app = Javalin.create().start(7000);
        RESTPfade restPfade = new RESTPfade();
        PortScanner portScanner = new PortScanner();

        HashMap<String, Method> methods = restPfade.getMap();
        methods.forEach((k, v) -> {
            if (!(v.getReturnType().getName().equals("void")))
            {
            //    app.get(k, ctx -> ctx.json(v.invoke( PortScanner.createCorrespondingObject(v))));
            }
            else
            {
                app.post(k, ctx -> {
                    String body = ctx.body();
                    Class<?>[] parameterTypes = v.getParameterTypes();
                    Class<?> param1 = parameterTypes[0];
                    Object paramObject = new Gson().fromJson(body, param1);
              //      v.invoke( PortScanner.createCorrespondingObject(v), paramObject);
                });
            }
        });

    }

    @Override
    public void start()
    {
        generate();
    }

    @Override
    public void stop()
    {
        app = null;         
    }
}
