package io.jexxa.jexxatest;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.jexxatest.application.JexxaITTestApplication;
import kong.unirest.GenericType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executors;

import static io.jexxa.jexxatest.JexxaTest.loadJexxaTestProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JexxaIntegrationTestIT {
    private static JexxaIntegrationTest jexxaIntegrationTest;

    @BeforeAll
    static void initBeforeAll()
    {
        //Start the application
        var result = Executors.newSingleThreadExecutor();
        result.execute(JexxaIntegrationTestIT::runApplication);

        //Connect the integration test to the application
        jexxaIntegrationTest = new JexxaIntegrationTest(JexxaITTestApplication.class);
    }

    @Test
    void testBoundedContextHandler()
    {
        //Arrange
        var boundedContext = jexxaIntegrationTest.getBoundedContext();

        //Act / Assert
        assertTrue( boundedContext.isRunning() );
        assertTrue( boundedContext.isHealthy() );

        assertNotNull( boundedContext.contextVersion() );
        assertNotNull( boundedContext.jexxaVersion() );
        assertNotNull( boundedContext.uptime() );
        assertNotNull( boundedContext.diagnostics() );

        assertEquals(JexxaITTestApplication.class.getSimpleName(), boundedContext.contextName() );
    }

    @Test
    void testSimpleValue()
    {
        //Arrange
        var simpleApplicationService = jexxaIntegrationTest.getRESTFulRPCHandler(SimpleApplicationService.class);

        //Act
        simpleApplicationService.postRequest("setSimpleValue", Void.class, 5);
        var result = simpleApplicationService.getRequest("getSimpleValue", Integer.class);

        //Assert
        assertEquals(5, result);
    }


    @Test
    void testSimpleValueObject()
    {
        //Arrange
        var simpleApplicationService = jexxaIntegrationTest.getRESTFulRPCHandler(SimpleApplicationService.class);
        var newValue = new JexxaValueObject(44);

        //Act
        simpleApplicationService.postRequest("setSimpleValueObject", Void.class, newValue);
        var result = simpleApplicationService.getRequest("getSimpleValueObject", JexxaValueObject.class);

        //Assert
        assertEquals(newValue, result);
    }

    @Test
    void testSetMessages()
    {
        //Arrange
        var simpleApplicationService = jexxaIntegrationTest.getRESTFulRPCHandler(SimpleApplicationService.class);
        var messageList = List.of("message1", "message2", "message3");

        //Act
        simpleApplicationService.postRequest("setMessages", Void.class, messageList);
        var result = simpleApplicationService.getRequest("getMessages", new GenericType<List<String>>(){});

        //Assert
        assertEquals(messageList, result);
    }


    static void runApplication()
    {
        JexxaITTestApplication.main(loadJexxaTestProperties());
    }


    @AfterAll
    static void tearDown()
    {
        JexxaITTestApplication.shutDown();
        jexxaIntegrationTest.shutDown();
    }

}
