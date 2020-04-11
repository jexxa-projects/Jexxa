package io.ddd.jexxa.infrastructure.drivingadapter.jmx;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import io.ddd.jexxa.application.applicationservice.SimpleApplicationService;
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

