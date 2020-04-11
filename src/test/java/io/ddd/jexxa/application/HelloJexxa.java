package io.ddd.jexxa.application;

import io.ddd.jexxa.core.JexxaMain;
import io.ddd.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;

public class HelloJexxa
{
    public static void main(String[] args)
    {
        JexxaMain jexxaMain = new JexxaMain("HelloJexxa").
                whiteList("io.ddd.jexxa");

        jexxaMain.bindToPort(JMXAdapter.class, jexxaMain.getBoundedContext());
        jexxaMain.bindToPort(RESTfulRPCAdapter.class, jexxaMain.getBoundedContext());

        jexxaMain.run();
    }
}


