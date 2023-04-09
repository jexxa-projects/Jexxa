package io.jexxa.jexxatest.integrationtest.rest;

import io.jexxa.testapplication.applicationservice.SimpleApplicationService;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.testapplication.domain.model.SpecialCasesValueObject;
import io.jexxa.jexxatest.JexxaIntegrationTest;
import io.jexxa.jexxatest.application.JexxaITTestApplication;
import kong.unirest.GenericType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RESTBindingIT {
    private static final JexxaIntegrationTest JEXXA_INTEGRATION_TEST = new JexxaIntegrationTest(JexxaITTestApplication.class);
    private static RESTBinding objectUnderTest;

    @BeforeAll
    static void initBeforeAll()
    {
        //Start the application
        var result = Executors.newSingleThreadExecutor();
        result.execute(RESTBindingIT::runApplication);

        //Connect the integration test to the application
        objectUnderTest = JEXXA_INTEGRATION_TEST.getRESTBinding();
    }

    @Test
    void testBoundedContextHandler()
    {
        //Arrange
        var boundedContext = objectUnderTest.getBoundedContext();

        //Act / Assert
        assertNotNull(JEXXA_INTEGRATION_TEST.getProperties());

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
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);

        //Act
        simpleApplicationService.postRequest(Void.class,"setSimpleValue", 5);

        var result = simpleApplicationService.getRequest(Integer.class, "getSimpleValue");

        //Assert
        assertEquals(5, result);
    }


    @Test
    void testSimpleValueObject()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);
        var newValue = new JexxaValueObject(44);

        //Act
        simpleApplicationService.postRequest(Void.class, "setSimpleValueObject", newValue);
        var result = simpleApplicationService.getRequest(JexxaValueObject.class, "getSimpleValueObject");

        //Assert
        assertEquals(newValue, result);
    }


    @Test
    void testThrowingGet()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);

        //Act / Assert
        assertDoesNotThrow(() -> simpleApplicationService.throwingPostRequest(Void.class, "setSimpleValueObject", new JexxaValueObject(44)));
        assertDoesNotThrow(() -> simpleApplicationService.throwingGetRequest(JexxaValueObject.class, "getSimpleValueObject"));
        assertDoesNotThrow(() -> simpleApplicationService.throwingGetRequest(new GenericType<List<String>>(){}, "getMessages"));
    }

    @Test
    void testSetMessages()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);
        var messageList = List.of("message1", "message2", "message3");

        //Act
        simpleApplicationService.postRequest(Void.class,"setMessages", messageList);
        var result = simpleApplicationService.getRequest(new GenericType<List<String>>(){}, "getMessages");

        //Assert
        assertEquals(messageList, result);
    }

    @Test
    void testSetSimpleValueObjectTwice()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);

        //Act
        simpleApplicationService.postRequest(Void.class, "setSimpleValueObjectTwice",
                new JexxaValueObject(44), new JexxaValueObject(88));

        var result = simpleApplicationService.getRequest(JexxaValueObject.class, "getSimpleValueObject");

        //Assert
        assertEquals(new JexxaValueObject(88), result);
    }

    @Test
    void testSetGetSimpleValue()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);
        simpleApplicationService.postRequest(Void.class, "setSimpleValue", 22);

        //Act
        var oldValue = simpleApplicationService.postRequest(Integer.class, "setGetSimpleValue", 44);
        var newValue = simpleApplicationService.getRequest(Integer.class, "getSimpleValue");

        //Assert
        assertEquals(22, oldValue);
        assertEquals(44, newValue);
    }

    @Test
    void testGetSpecialCasesValueObject()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);

        //Act
        var result = simpleApplicationService.getRequest(SpecialCasesValueObject.class, "getSpecialCasesValueObject");

        //Assert
        assertEquals(SpecialCasesValueObject.SPECIAL_CASES_VALUE_OBJECT, result);
    }


    @Test
    void testThrowExceptionTest()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);

        //Act / Assert
        assertThrows(SimpleApplicationService.SimpleApplicationException.class,
                () -> simpleApplicationService.throwingPostRequest(Void.class,"throwExceptionTest")
        );
    }

    @Test
    void testUnknownMethod()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);

        assertThrows(BadRequestException.class,
                () -> simpleApplicationService.getRequest(Integer.class,"unknownMethod")
        );
    }

    @Test
    void testThrowNullPointerException()
    {
        //Arrange
        var simpleApplicationService = objectUnderTest.getRESTHandler(SimpleApplicationService.class);

        //Act / Assert
        assertThrows(NullPointerException.class,
                () -> simpleApplicationService.getRequest(Integer.class,"throwNullPointerException")
        );
    }


    static void runApplication()
    {
        JexxaITTestApplication.main(JEXXA_INTEGRATION_TEST.getProperties());
    }


    @AfterAll
    static void tearDown()
    {
        JexxaITTestApplication.shutDown();
        JEXXA_INTEGRATION_TEST.shutDown();
    }
}
