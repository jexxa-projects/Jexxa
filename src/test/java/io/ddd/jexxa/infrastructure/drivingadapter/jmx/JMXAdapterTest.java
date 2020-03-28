package io.ddd.jexxa.infrastructure.drivingadapter.jmx;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import io.ddd.jexxa.dummyapplication.applicationservice.SimpleApplicationService;
import org.junit.Test;

public class JMXAdapterTest
{
    @Test
    public void registerApplicationService()
    {
        //Arrange
        var defaultValue = 42;
        var simpleApplicationService = new SimpleApplicationService(defaultValue);
        var objectUnderTest = new JMXAdapter();
        System.setProperty("com.sun.management.jmxremote.host", "localhost");
        System.setProperty("com.sun.management.jmxremote.port", "62345");
        System.setProperty("com.sun.management.jmxremote.rmi.port", "62345");
        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
        System.setProperty("com.sun.management.jmxremote.ssl", "false");

        System.setProperty("com.sun.management.jmx.port", "62345");

        //Act
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();


        //Assert
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> result = mbs.queryMBeans(null , null);

        assertNotNull(result);
        assertTrue(result.
                stream().
                anyMatch(element -> element.getClassName().endsWith(SimpleApplicationService.class.getSimpleName()))
        );

        objectUnderTest.stop();
    }
}
