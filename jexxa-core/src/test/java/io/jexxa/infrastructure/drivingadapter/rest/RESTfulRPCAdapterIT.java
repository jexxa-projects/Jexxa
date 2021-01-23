package io.jexxa.infrastructure.drivingadapter.rest;

import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.HTTP_PORT_PROPERTY;
import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.STATIC_FILES_ROOT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.domain.valueobject.SpecialCasesValueObject;
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
    private static final String STATIC_TEST_PAGE = "http://localhost:7000/index.html";


    private static final int DEFAULT_VALUE = 42;
    private final SimpleApplicationService simpleApplicationService = new SimpleApplicationService();

    private RESTfulRPCAdapter objectUnderTest;

    @BeforeEach
    void setupTests()
    {
        //Setup
        simpleApplicationService.setSimpleValue(42);

        var properties = new Properties();
        var defaultHost = "localhost";
        var defaultPort = 7000;

        properties.put(RESTfulRPCAdapter.HOST_PROPERTY, defaultHost);
        properties.put(HTTP_PORT_PROPERTY, Integer.toString(defaultPort));
        properties.put(STATIC_FILES_ROOT, "/public");

        objectUnderTest = RESTfulRPCAdapter.createAdapter(properties);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();
    }

    @AfterEach
    void tearDownTests()
    {
        //tear down
        objectUnderTest.stop();
        objectUnderTest = null;
        Unirest.shutDown();
    }


    @Test
        // RPC call test: int getSimpleValue()
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
        assertEquals(simpleApplicationService.getSimpleValue(), result.intValue());
    }

    @Test
    void testWithRandomPort()
    {
        //Arrange
        Properties properties = new Properties();
        properties.setProperty(HTTP_PORT_PROPERTY, String.valueOf(0));

        var secondAdapter = RESTfulRPCAdapter.createAdapter(properties);
        secondAdapter.register(simpleApplicationService);
        secondAdapter.start();
        var secondRestPath = "http://localhost:" + secondAdapter.getHTTPPort() + "/SimpleApplicationService/";


        //Act using secondAdapter
        Integer result = Unirest.get(secondRestPath + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();


        secondAdapter.stop();

        //Assert
        assertNotNull(result);
        assertEquals(DEFAULT_VALUE, simpleApplicationService.getSimpleValue());
        assertEquals(simpleApplicationService.getSimpleValue(), result.intValue());

    }

    @Test
    void testUnsetProperties()
    {
        //Arrange
        var properties = new Properties();

        //Act and Assert
        assertThrows(IllegalArgumentException.class, () -> RESTfulRPCAdapter.createAdapter(properties));
    }


    @Test
        // RPC call test: void setSimpleValue(44)
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

    @Test
        // RPC call test: void setSimpleValueObject(SimpleValueObject(44))
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

    @Test
        // RPC call test: void setMessages(List<String>)
    void testPOSTCommandWithList()
    {
        //Arrange
        var messageList = List.of("message1", "message2", "message3");

        //Act
        var response = Unirest.post(REST_PATH + "setMessages")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(messageList)
                .asEmpty();

        //Assert
        assertTrue(response.isSuccess());
        assertEquals(messageList, simpleApplicationService.getMessages());
    }

    @Test
        // RPC call test: void setValueObjectsAndMessages
    void testPOSTCommandWithMultipleLists()
    {
        //Arrange
        var valueObjectList = List.of(new JexxaValueObject(1), new JexxaValueObject(2), new JexxaValueObject(3));
        var messageList = List.of("message1", "message2", "message3");

        //Act
        Gson gson = new Gson();
        var jsonArray = new JsonArray();
        jsonArray.add(gson.toJsonTree(valueObjectList));
        jsonArray.add(gson.toJsonTree(messageList));

        var response = Unirest.post(REST_PATH + "setValueObjectsAndMessages")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(jsonArray)
                .asEmpty();

        //Assert
        assertTrue(response.isSuccess());
        assertEquals(messageList, simpleApplicationService.getMessages());
    }

    @Test
        // RPC call test: void setSimpleValueObjectTwice(SimpleValueObject(44), SimpleValueObject(88))
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

    @Test
        // RPC call test:  int setGetSimpleValue(44)
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

    @Test
        // RPC call test: int getSimpleValue()
    void testGETCommandWithSpecialCasesValueObject()
    {
        //Arrange -> Nothing to do

        //Act
        SpecialCasesValueObject result = Unirest.get(REST_PATH + "getSpecialCasesValueObject")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(SpecialCasesValueObject.class).getBody();


        //Assert
        assertNotNull(result);
        assertEquals(SpecialCasesValueObject.SPECIAL_CASES_VALUE_OBJECT, result);
    }

    @Test
        // RPC call test: int getSimpleValue()
    void testGETCommandWithNullPointerException()
    {
        //Arrange -> Nothing to do
        var gson = new Gson();

        //Act
        var response = Unirest.get(REST_PATH + "throwNullPointerException")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asJson();
        JsonObject error = response.mapError(JsonObject.class);

        var exceptionType = error.get("ExceptionType").getAsString();
        var exception = error.get("Exception").getAsString();
        var nullPointerException = gson.fromJson(exception, NullPointerException.class);

        //Assert
        assertNotNull(error);
        assertNotNull(exceptionType);
        assertNotNull(exception);

        assertEquals(NullPointerException.class.getName(), exceptionType);
        assertNotNull(nullPointerException.getStackTrace());
        assertTrue(nullPointerException.getStackTrace().length > 0);
    }

    @Test
    void testStaticWebPage()
    {
        //Act
        var response = Unirest.get(STATIC_TEST_PAGE)
                .asEmpty();

        //Assert
        assertTrue(response.isSuccess());
    }
}