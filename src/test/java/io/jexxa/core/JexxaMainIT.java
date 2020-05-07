package io.jexxa.core;


import static io.jexxa.TestTags.INTEGRATION_TEST;

import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;
import io.jexxa.application.applicationservice.JexxaApplicationService;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domainservice.InitializeJexxaAggregates;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(INTEGRATION_TEST)
public class JexxaMainIT
{
    private Properties properties;
    private JexxaMain objectUnderTest;
    private final String contextName = "HelloJexxa";

    @BeforeEach
    public void initTests()
    {
        properties = new Properties();
        properties.putAll(getRESTfulRPCProperties());
    }

    @AfterEach
    public void tearDownTests()
    {
        if (objectUnderTest != null)
        {
            objectUnderTest.stop();
        }
        Unirest.shutDown();
    }


    
    @Test
    public void bindToPort()
    {
        //Arrange
        objectUnderTest = new JexxaMain(contextName, properties);


        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest
                .bind(JMXAdapter.class).to(SimpleApplicationService.class)
                .bind(RESTfulRPCAdapter.class).to(SimpleApplicationService.class)

                .start();

        //Assert
        assertJMXAdapter(SimpleApplicationService.class);
        assertRESTfulRPCAdapter();
    }

    @Test
    public void bindToPortWithDrivenAdapter()
    {
        //Arrange
        objectUnderTest = new JexxaMain(contextName, properties);

        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest
                .addToInfrastructure("io.jexxa.application.infrastructure")
                .bind(JMXAdapter.class).to(ApplicationServiceWithDrivenAdapters.class)
                .bind(RESTfulRPCAdapter.class).to(ApplicationServiceWithDrivenAdapters.class)
                .start();


        //Assert
        assertJMXAdapter(ApplicationServiceWithDrivenAdapters.class);
    }
    

    @Test
    public void bindToAnnotatedPorts()
    {
        //Arrange
        objectUnderTest = new JexxaMain(contextName, properties);

        //Act: Bind all DrivingAdapter to all ApplicationServices
        objectUnderTest
                .bind(RESTfulRPCAdapter.class).toAnnotation(ApplicationService.class)
                .start();

        //Assert
        assertRESTfulRPCAdapter();
    }


    @Test
    public void bootstrapService()
    {
        //Arrange
        objectUnderTest = new JexxaMain(contextName, properties);

        //Act
        objectUnderTest
                .addToInfrastructure("io.jexxa.application.infrastructure")
                .bootstrap(InitializeJexxaAggregates.class).with(InitializeJexxaAggregates::initDomainData);

        var jexxaApplicationService = objectUnderTest.getInstanceOfPort(JexxaApplicationService.class);

        //Assert 
        Assertions.assertTrue(jexxaApplicationService.getAggregateCount() > 0);
    }


    /* ---------------------Util methods ------------------ */
    void assertJMXAdapter(Class<?> clazz) {
        //Assert
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> result = mbs.queryMBeans(null , null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.
                stream().
                anyMatch(element -> element.getClassName().endsWith(clazz.getSimpleName()))
        );
    }

    void assertRESTfulRPCAdapter() {
        //Assert
        String restPath = "http://"
                + properties.get(RESTfulRPCAdapter.HOST_PROPERTY) + ":"
                + properties.get(RESTfulRPCAdapter.PORT_PROPERTY) + "/"
                + SimpleApplicationService.class.getSimpleName() + "/"
                + "getSimpleValue";
        
        Integer result = Unirest.get(restPath)
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(42, result);
    }



    Properties getRESTfulRPCProperties() {
        Properties properties = new Properties();
        properties.put(RESTfulRPCAdapter.HOST_PROPERTY, "localhost");
        properties.put(RESTfulRPCAdapter.PORT_PROPERTY, Integer.toString(7000));
        return properties;
    }
}
