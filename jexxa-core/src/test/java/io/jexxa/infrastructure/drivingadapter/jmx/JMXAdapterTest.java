package io.jexxa.infrastructure.drivingadapter.jmx;


import static io.jexxa.infrastructure.drivingadapter.jmx.MBeanConvention.JEXXA_CONTEXT_NAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class JMXAdapterTest
{
    @Test
    void registerApplicationService()
    {
        //Arrange
        var defaultValue = 42;
        var properties = new Properties();
        properties.put(JEXXA_CONTEXT_NAME, "registerApplicationService"); // Unique name
        var simpleApplicationService = new SimpleApplicationService();
        simpleApplicationService.setSimpleValue(defaultValue);

        var objectUnderTest = new JMXAdapter(properties);
        
        //Act
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();


        //Assert that mbean service is registered  
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> result = mbs.queryMBeans(null , null);

        assertNotNull(result);
        assertTrue(result
                .stream()
                .anyMatch(element -> element
                        .getClassName()
                        .endsWith(SimpleApplicationService.class.getSimpleName())
                )
        );

        objectUnderTest.stop();
    }

    @Test
    void throwExceptionWhenRegisterApplicationServiceTwice()
    {
        //Arrange
        var defaultValue = 42;
        var properties = new Properties();
        properties.put(JEXXA_CONTEXT_NAME, "throwExceptionWhenRegisterApplicationServiceTwice"); // Unique name
        
        var simpleApplicationService = new SimpleApplicationService();
        simpleApplicationService.setSimpleValue(defaultValue);

        var objectUnderTest = new JMXAdapter(properties);

        //Act
        objectUnderTest.register(simpleApplicationService);
        assertThrows(IllegalArgumentException.class, () -> objectUnderTest.register(simpleApplicationService));
        objectUnderTest.stop();
    }
}

