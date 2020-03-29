package io.ddd.jexxa.dummyapplication;

import io.ddd.jexxa.core.JexxaMain;
import io.ddd.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;

public class HelloJexxa
{
    public static void main(String[] args)
    {
        setJMXProperties();
        JexxaMain jexxaMain = new JexxaMain().
                whiteListPackage("io.ddd.jexxa");
                            
        jexxaMain.bind(JMXAdapter.class, jexxaMain.getBoundedContext());

        jexxaMain.run();
    }

    static void setJMXProperties() {
        System.setProperty("com.sun.management.jmxremote.host", "localhost");
        System.setProperty("com.sun.management.jmxremote.port", "62345");
        System.setProperty("com.sun.management.jmxremote.rmi.port", "62345");
        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
        System.setProperty("com.sun.management.jmxremote.ssl", "false");
    }
}
