package io.ddd.jexxa.dummyapplication;

import io.ddd.jexxa.core.JexxaMain;
import io.ddd.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;

public class HelloJexxa
{
    public static void main(String[] args)
    {
        JexxaMain jexxaMain = new JexxaMain().
                whiteListPackage("io.ddd.jexxa");

        jexxaMain.bind(JMXAdapter.class, jexxaMain.getBoundedContext());
        jexxaMain.bind(RESTfulRPCAdapter.class, jexxaMain.getBoundedContext());

        //        jexxaMain.bindDrivingAdapter(JMXAdapter.class);
        //        jexxaMain.bindDrivingAdapter(JMXAdapter.class, jexxaMain.getBoundedContext());
        //        jexxaMain.bindDrivingAdapter(JMXAdapter.class, JMXFacade.class,);



        jexxaMain.run();
    }
}


