package io.jexxa.drivingadapter.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.jexxa.TestConstants;
import io.jexxa.common.wrapper.json.JSONManager;
import io.jexxa.testapplication.applicationservice.SimpleApplicationService;
import io.jexxa.testapplication.domain.model.JexxaRecord;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.testapplication.domain.model.SpecialCasesValueObject;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("SameParameterValue")
@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class RESTfulRPCAdapterIT
{
    private static final String METHOD_GET_SIMPLE_VALUE = "getSimpleValue";

    private static final String REST_PATH = "http://localhost:7500/SimpleApplicationService/";
    private static final String STATIC_TEST_PAGE = "http://localhost:7500/index.html";


    private static final int DEFAULT_VALUE = 42;
    private final SimpleApplicationService simpleApplicationService = new SimpleApplicationService();

    private RESTfulRPCAdapter objectUnderTest;
    private final Properties properties = new Properties();

    @BeforeEach
    void setupTests()
    {
        //Setup
        simpleApplicationService.setSimpleValue(42);

        var defaultHost = "localhost";
        var defaultPort = 7500;

        properties.put(JexxaWebProperties.JEXXA_REST_HOST, defaultHost);
        properties.put(JexxaWebProperties.JEXXA_REST_PORT, Integer.toString(defaultPort));
        properties.put(JexxaWebProperties.JEXXA_REST_STATIC_FILES_ROOT, "/public");

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
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
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
        properties.setProperty(JexxaWebProperties.JEXXA_REST_PORT, String.valueOf(0));

        var secondAdapter = RESTfulRPCAdapter.createAdapter(properties);
        secondAdapter.register(simpleApplicationService);
        secondAdapter.start();
        var secondRestPath = "http://localhost:" + secondAdapter.getHTTPPort() + "/SimpleApplicationService/";


        //Act using secondAdapter
        Integer result = Unirest.get(secondRestPath + METHOD_GET_SIMPLE_VALUE)
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
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


    @Test  // RPC call test: void setSimpleValue(44)
    void testPOSTCommandWithOneAttribute()
    {
        //Arrange
        var newValue = 44;

        //Act
        var response = Unirest.post(REST_PATH + "setSimpleValue")
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .body(newValue)
                .asJson();

        //Assert
        Integer newResult = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
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
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .body(newValue)
                .asJson();

        //Assert
        Integer newResult = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        assertTrue(response.isSuccess());
        assertEquals(newValue.getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(newValue.getValue(), newResult.intValue());
    }

    @Test // RPC call test: void setSimpleValueObject(SimpleValueObject(44))
    void testPOSTCommandWithRecordList()
    {
        //Arrange
        var newValue = List.of(new JexxaRecord("JexxaRecord1"), new JexxaRecord("JexxaRecord2"), new JexxaRecord("JexxaRecord3"));

        //Act
        var response = Unirest.post(REST_PATH + "setJexxaRecordList")
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .body(newValue)
                .asJson();

        //Assert
        var newResult = Unirest.get(REST_PATH + "getJexxaRecordList")
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .asObject(new GenericType<List<JexxaRecord>>() {
                }).getBody();

        assertTrue(response.isSuccess());
        assertEquals(newValue, simpleApplicationService.getJexxaRecordList());
        assertEquals(newValue, newResult);
    }
    @Test  // RPC call test: void setMessages(List<String>)
    void testPOSTCommandWithList()
    {
        //Arrange
        var messageList = List.of("message1", "message2", "message3");

        //Act
        var response = Unirest.post(REST_PATH + "setMessages")
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .body(messageList)
                .asEmpty();

        //Assert
        assertTrue(response.isSuccess());
        assertEquals(messageList, simpleApplicationService.getMessages());
    }

    @Test  // RPC call test: void setValueObjectsAndMessages
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
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .body(jsonArray)
                .asEmpty();

        var result = Unirest.get(REST_PATH + "getMessages")
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .asObject(new GenericType<List<String>>() {/*Empty anonymous class*/} ).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(messageList, simpleApplicationService.getMessages());
        assertEquals(result, simpleApplicationService.getMessages());
    }

    @Test // RPC call test: void setSimpleValueObjectTwice(SimpleValueObject(44), SimpleValueObject(88))
    void testPOSTCommandWithTwoObjects()
    {
        //Arrange
        var paramList = new JexxaValueObject[]{new JexxaValueObject(44), new JexxaValueObject(88)};

        //Act
        var response = Unirest.post(REST_PATH + "setSimpleValueObjectTwice")
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .body(paramList)
                .asEmpty();

        //Assert
        Integer newResult = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
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
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .body(newValue)
                .asObject(Integer.class).getBody();


        //Act
        //Assert
        Integer newResult = Unirest.get(REST_PATH + METHOD_GET_SIMPLE_VALUE)
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
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
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .asJson();
        JsonObject error = response.mapError(JsonObject.class);

        //Assert
        assertNotNull(error);
        assertEquals(SimpleApplicationService.SimpleApplicationException.class.getName(), error.get("ExceptionType").getAsString());

        var jsonString = error.get("Exception").getAsString();

        assertThrows(SimpleApplicationService.SimpleApplicationException.class, () -> {
            throw JSONManager.getJSONConverter().fromJson(jsonString, SimpleApplicationService.SimpleApplicationException.class);
        });
    }

    @Test
    void testGETCommandWithSpecialCasesValueObject()
    {
        //Arrange -> Nothing to do

        //Act
        SpecialCasesValueObject result = Unirest.get(REST_PATH + "getSpecialCasesValueObject")
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .asObject(SpecialCasesValueObject.class).getBody();


        //Assert
        assertNotNull(result);
        assertEquals(SpecialCasesValueObject.SPECIAL_CASES_VALUE_OBJECT, result);
    }

    @Test
    void testGETCommandWithNullPointerException()
    {
        //Arrange
        var gson = JSONManager.getJSONConverter();

        //Act
        var response = Unirest.get(REST_PATH + "throwNullPointerException")
                .header(RESTConstants.CONTENT_TYPE, RESTConstants.APPLICATION_TYPE)
                .asJson();
        JsonObject error = response.mapError(JsonObject.class);

        var exceptionType = error.get("ExceptionType").getAsString();
        var exception = error.get("Exception").getAsString();
        var nullPointerException = gson.fromJson(exception, NullPointerException.class);

        //Assert
        assertNotNull(error);
        assertNotNull(exceptionType);
        assertNotNull(exception);
        assertNotNull(nullPointerException);

        assertEquals(NullPointerException.class.getName(), exceptionType);
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

    @Test
    void testStaticWebPageExternalPath()
    {
        //Arrange
        objectUnderTest.stop();
        properties.put(JexxaWebProperties.JEXXA_REST_STATIC_FILES_ROOT, "src/test/resources/public/");
        properties.put(JexxaWebProperties.JEXXA_REST_STATIC_FILES_EXTERNAL, "true");

        objectUnderTest = RESTfulRPCAdapter.createAdapter(properties);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        //Act
        var response = Unirest.get(STATIC_TEST_PAGE)
                .asEmpty();

        //Assert
        assertTrue(response.isSuccess());
    }

    @AfterEach
    void tearDown()
    {
        objectUnderTest.stop();
    }

}