package io.jexxa.core;



import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.TestConstants.JEXXA_DRIVEN_ADAPTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import io.jexxa.TestConstants;
import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;
import io.jexxa.application.applicationservice.JexxaApplicationService;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domainservice.InitializeJexxaAggregates;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JexxaMainIT
{
    private JexxaMain objectUnderTest;
    private final String contextName = "HelloJexxa";

    @BeforeEach
    void initTests()
    {
        objectUnderTest = new JexxaMain(contextName);
        objectUnderTest.addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .addToApplicationCore(JEXXA_APPLICATION_SERVICE);    }

    @AfterEach
    void tearDownTests()
    {
        if (objectUnderTest != null)
        {
            objectUnderTest.stop();
        }
        Unirest.shutDown();
    }


    
    @Test
    void bindToPort()
    {
        //Arrange - All done in initTests
        
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
    void bindToPortWithDrivenAdapter()
    {
        //Arrange - All done in initTests

        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMXAdapter.class).to(ApplicationServiceWithDrivenAdapters.class)
                .bind(RESTfulRPCAdapter.class).to(ApplicationServiceWithDrivenAdapters.class)
                .start();


        //Assert
        assertJMXAdapter(ApplicationServiceWithDrivenAdapters.class);
    }
    

    @Test
    void bindToAnnotatedPorts()
    {
        //Arrange - All done in initTests
        
        //Act: Bind all DrivingAdapter to all ApplicationServices
        objectUnderTest
                .bind(RESTfulRPCAdapter.class).toAnnotation(ApplicationService.class)
                .start();

        //Assert
        assertRESTfulRPCAdapter();
    }


    @Test
    void bootstrapService()
    {
        //Arrange
        objectUnderTest = new JexxaMain(contextName);
        objectUnderTest.addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .addToApplicationCore(JEXXA_APPLICATION_SERVICE);

        //Act
        objectUnderTest
                .bootstrap(InitializeJexxaAggregates.class).with(InitializeJexxaAggregates::initDomainData);

        var jexxaApplicationService = objectUnderTest.getInstanceOfPort(JexxaApplicationService.class);

        //Assert 
        assertTrue(jexxaApplicationService.getAggregateCount() > 0);
    }


    /* ---------------------Util methods ------------------ */
    void assertJMXAdapter(Class<?> clazz) {
        //Assert
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> result = mbs.queryMBeans(null , null);

        assertNotNull(result);
        assertTrue(result.
                stream().
                anyMatch(element -> element.getClassName().endsWith(clazz.getSimpleName()))
        );
    }

    void assertRESTfulRPCAdapter() {
        //Assert
        String restPath = "http://"
                + objectUnderTest.getProperties().get(RESTfulRPCAdapter.HOST_PROPERTY) + ":"
                + objectUnderTest.getProperties().get(RESTfulRPCAdapter.PORT_PROPERTY) + "/"
                + SimpleApplicationService.class.getSimpleName() + "/"
                + "getSimpleValue";
        
        Integer result = Unirest.get(restPath)
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();

        assertNotNull(result);
        assertEquals(42, result);
    }

}
