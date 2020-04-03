package io.ddd.jexxa.dummyapplication;

import static io.ddd.jexxa.utils.ThrowingConsumer.exceptionLogger;

import java.lang.reflect.Array;
import java.util.Arrays;

import io.ddd.jexxa.core.JexxaMain;
import io.ddd.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;

public class HelloJexxa
{
    public static void main(String[] args)
    {
        JexxaMain jexxaMain = new JexxaMain("HelloJexxa").
                whiteListPackage("io.ddd.jexxa");

        jexxaMain.bind(JMXAdapter.class, jexxaMain.getBoundedContext());
        jexxaMain.bind(RESTfulRPCAdapter.class, jexxaMain.getBoundedContext());

        jexxaMain.run();
    }
}


