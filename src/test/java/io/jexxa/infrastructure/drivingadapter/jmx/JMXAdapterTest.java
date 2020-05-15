package io.jexxa.infrastructure.drivingadapter.jmx;


import static io.jexxa.TestTags.UNIT_TEST;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(UNIT_TEST)
public class JMXAdapterTest
{
    @Test
    public void registerApplicationService()
    {
        //Arrange
        var defaultValue = 42;
        var simpleApplicationService = new SimpleApplicationService();
        simpleApplicationService.setSimpleValue(defaultValue);

        var objectUnderTest = new JMXAdapter();
        
        //Act
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();


        //Assert that mbean service is registered  
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> result = mbs.queryMBeans(null , null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result
                .stream()
                .anyMatch(element -> element
                        .getClassName()
                        .endsWith(SimpleApplicationService.class.getSimpleName())
                )
        );

        objectUnderTest.stop();
    }

    
}

