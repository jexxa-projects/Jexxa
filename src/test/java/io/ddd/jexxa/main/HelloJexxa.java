package io.ddd.jexxa.main;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.core.Jexxa;
import io.ddd.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import org.junit.Before;
import org.junit.Test;

public class HelloJexxa
{
    @Before
    public void initTests()
    {
        System.setProperty("com.sun.management.jmxremote.host", "localhost");
        System.setProperty("com.sun.management.jmxremote.port", "62345");
        System.setProperty("com.sun.management.jmxremote.rmi.port", "62345");
        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
        System.setProperty("com.sun.management.jmxremote.ssl", "false");
    }

    
    @Test
    public void simpleHelloJexxa()
    {
        //Arrange
        Jexxa jexxa = new Jexxa();


        //Act: Bind a concrete class to a concrete DrivingAdapter to a port
        jexxa.bind(JMXAdapter.class, SimpleApplicationService.class);

        
        //Assert
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> result = mbs.queryMBeans(null , null);

        assertNotNull(result);
        assertTrue(result.
                stream().
                anyMatch(element -> element.getClassName().endsWith(SimpleApplicationService.class.getSimpleName()))
        );
    }
}
