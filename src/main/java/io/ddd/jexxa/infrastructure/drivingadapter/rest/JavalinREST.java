package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import io.ddd.jexxa.infrastructure.PortScanner;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.stereotype.applicationcore.ApplicationService;
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

        List<Class<?>> applicationServiceList = portScanner.findAnnotation(ApplicationService.class);

        List<RESTPfade.RestURL> allMethods = new ArrayList<>();
        applicationServiceList
                .stream()
                .forEach(element -> allMethods.addAll(RESTPfade.getRestURLs(element)));



        allMethods.forEach( element -> {
            if (!(element.getMethod().getReturnType().getName().equals("void")))
            {
                //app.get(element.getRestURL(), ctx -> ctx.json(v.invoke( PortScanner.createCorrespondingObject(v))));
            }
            else
            {
                app.post(element.getRestURL(), ctx -> {
                    String body = ctx.body();
                    Class<?>[] parameterTypes = element.getMethod().getParameterTypes();
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
