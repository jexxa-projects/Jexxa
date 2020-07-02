package io.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@SuppressWarnings("SameParameterValue")
@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class RESTfulRPCAdapterIT
{
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_TYPE = "application/json";
    private static final String METHOD_GET_SIMPLE_VALUE = "getSimpleValue";

    private static final String REST_PATH = "http://localhost:7000/SimpleApplicationService/";

    private static final int DEFAULT_VALUE = 42;
    private final SimpleApplicationService simpleApplicationService = new SimpleApplicationService();

    private RESTfulRPCAdapter objectUnderTest;

    @BeforeEach
    void setupTests(){
        //Setup
        simpleApplicationService.setSimpleValue(42);

        var properties = new Properties();
        var defaultHost = "localhost";
        var defaultPort = 7000;

        properties.put(RESTfulRPCAdapter.HOST_PROPERTY, defaultHost);
        properties.put(RESTfulRPCAdapter.PORT_PROPERTY, Integer.toString(defaultPort));

        objectUnderTest = new RESTfulRPCAdapter(properties);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();
    }

    @AfterEach
    void tearDownTests(){
        //tear down
        objectUnderTest.stop();
        objectUnderTest = null;
        Unirest.shutDown();
    }


    @Test // RPC call test: int getSimpleValue()
    void testGETCommand()
    {
        //Arrange -> Nothing to do  

        //Act
        Integer result = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();


        //Assert
        assertNotNull(result);
        assertEquals(DEFAULT_VALUE, simpleApplicationService.getSimpleValue());
        assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );
    }

    @Test
    void testWithRandomPort()
    {
        //Arrange
        var secondAdapter = new RESTfulRPCAdapter("localhost",0);
        secondAdapter.register(simpleApplicationService);
        secondAdapter.start();
        var secondRestPath = "http://localhost:" + secondAdapter.getPort() + "/SimpleApplicationService/";


        //Act using secondAdapter 
        Integer result = Unirest.get(secondRestPath + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();


        secondAdapter.stop();

        //Assert
        assertNotNull(result);
        assertEquals(DEFAULT_VALUE, simpleApplicationService.getSimpleValue());
        assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );

    }

    @Test
    void testUnsetProperties()
    {
        //Arrange
        var secondAdapter = new RESTfulRPCAdapter(new Properties());
        secondAdapter.register(simpleApplicationService);
        secondAdapter.start();
        var secondRestPath = "http://localhost:" + secondAdapter.getPort() + "/SimpleApplicationService/";


        //Act using secondAdapter
        Integer result = Unirest.get(secondRestPath + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();


        secondAdapter.stop();

        //Assert
        assertNotNull(result);
        assertEquals(DEFAULT_VALUE, simpleApplicationService.getSimpleValue());
        assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );
    }

    @Test  // RPC call test: void setSimpleValue(44)
    void testPOSTCommandWithOneAttribute()
    {
        //Arrange
        var newValue = 44;

        //Act
        var response = Unirest.post(REST_PATH + "setSimpleValue")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(newValue)
                .asJson();

        //Assert
        Integer newResult = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        assertTrue(response.isSuccess());
        assertEquals(newValue, simpleApplicationService.getSimpleValue());
        assertEquals(newValue, newResult.intValue());
    }

    @Test // RPC call test: void setSimpleValueObject(SimpleValueObject(44))
    void testPOSTCommandWithOneObject()
    {
        //Arrange
        var newValue = new JexxaValueObject(44);

        //Act
        var response = Unirest.post(REST_PATH + "setSimpleValueObject")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(newValue)
                .asJson();

        //Assert
        Integer newResult = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        assertTrue(response.isSuccess());
        assertEquals(newValue.getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(newValue.getValue(), newResult.intValue());
    }

    @Test // RPC call test: void setSimpleValueObjectTwice(SimpleValueObject(44), SimpleValueObject(88))
    void testPOSTCommandWithTwoObjects()
    {
        //Arrange
        var paramList = new JexxaValueObject[]{new JexxaValueObject(44), new JexxaValueObject(88)};

        //Act
        var response = Unirest.post(REST_PATH + "setSimpleValueObjectTwice")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(paramList)
                .asEmpty();

        //Assert
        Integer newResult = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        assertTrue(response.isSuccess());
        assertEquals(paramList[1].getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(paramList[1].getValue(), newResult.intValue());
    }

    @Test // RPC call test:  int setGetSimpleValue(44)
    void testPOSTCommandWithReturnValue()
    {
        //Arrange
        var newValue = 44;

        //Act
        var oldValue = Unirest.post(REST_PATH + "setGetSimpleValue")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(newValue)
                .asObject(Integer.class).getBody();


        //Act
        //Assert
        Integer newResult = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        //Assert
        assertNotNull(oldValue);
        assertEquals(DEFAULT_VALUE, oldValue.intValue());
        assertEquals(newValue, simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(newValue, newResult.intValue());
    }

    @Test
    void testPOSTCommandWithException()
    {
        //Arrange

        //Act
        var response = Unirest.post(REST_PATH + "throwExceptionTest")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asJson();
        JsonObject error = response.mapError(JsonObject.class);

        //Assert
        assertNotNull(error);
        assertEquals(SimpleApplicationService.SimpleApplicationException.class.getName(), error.get("ExceptionType").getAsString());

        var jsonString = error.get("Exception").getAsString();
        var gson = new Gson();
        assertThrows(SimpleApplicationService.SimpleApplicationException.class, () -> {
            throw gson.fromJson(jsonString, SimpleApplicationService.SimpleApplicationException.class);
        });
    }
}
