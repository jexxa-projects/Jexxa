package io.jexxa.infrastructure.drivingadapter.rest;

import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jexxa.TestTags;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@SuppressWarnings("SameParameterValue")
@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestTags.INTEGRATION_TEST)
class RESTfulRPCAdapterIT
{
    static final String CONTENT_TYPE = "Content-Type";
    static final String APPLICATION_TYPE = "application/json";
    static final String METHOD_GET_SIMPLE_VALUE = "getSimpleValue";

    final int defaultPort = 7000;
    final String defaultHost = "localhost";
    Properties properties;
    final String restPath = "http://localhost:7000/SimpleApplicationService/";

    final int defaultValue = 42;
    final SimpleApplicationService simpleApplicationService = new SimpleApplicationService();

    RESTfulRPCAdapter objectUnderTest;

    @BeforeEach
    void setupTests(){
        //Setup
        simpleApplicationService.setSimpleValue(42);

        properties = new Properties();
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
        Integer result = Unirest.get(restPath + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();


        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        Assertions.assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );
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
        Assertions.assertNotNull(result);
        Assertions.assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        Assertions.assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );

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
        Assertions.assertNotNull(result);
        Assertions.assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        Assertions.assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );
    }

    @Test  // RPC call test: void setSimpleValue(44)
    void testPOSTCommandWithOneAttribute()
    {
        //Arrange
        var newValue = 44;

        //Act
        var response = Unirest.post(restPath + "setSimpleValue")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(newValue)
                .asJson();

        //Assert
        Integer newResult = Unirest.get(restPath + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(newValue, simpleApplicationService.getSimpleValue());
        Assertions.assertEquals(newValue, newResult.intValue());
    }

    @Test // RPC call test: void setSimpleValueObject(SimpleValueObject(44))
    void testPOSTCommandWithOneObject()
    {
        //Arrange
        var newValue = new JexxaValueObject(44);

        //Act
        var response = Unirest.post(restPath + "setSimpleValueObject")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(newValue)
                .asJson();

        //Assert
        Integer newResult = Unirest.get(restPath + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(newValue.getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        Assertions.assertEquals(newValue.getValue(), newResult.intValue());
    }

    @Test // RPC call test: void setSimpleValueObjectTwice(SimpleValueObject(44), SimpleValueObject(88))
    void testPOSTCommandWithTwoObjects()
    {
        //Arrange
        var paramList = new JexxaValueObject[]{new JexxaValueObject(44), new JexxaValueObject(88)};

        //Act
        var response = Unirest.post(restPath + "setSimpleValueObjectTwice")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(paramList)
                .asEmpty();

        //Assert
        Integer newResult = Unirest.get(restPath + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(paramList[1].getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        Assertions.assertEquals(paramList[1].getValue(), newResult.intValue());
    }

    @Test // RPC call test:  int setGetSimpleValue(44)
    void testPOSTCommandWithReturnValue()
    {
        //Arrange
        var newValue = 44;

        //Act
        var oldValue = Unirest.post(restPath + "setGetSimpleValue")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(newValue)
                .asObject(Integer.class).getBody();


        //Act
        //Assert
        Integer newResult = Unirest.get(restPath + METHOD_GET_SIMPLE_VALUE)
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Integer.class).getBody();

        //Assert
        Assertions.assertNotNull(oldValue);
        Assertions.assertEquals(defaultValue, oldValue.intValue());
        Assertions.assertEquals(newValue, simpleApplicationService.getSimpleValueObject().getValue());
        Assertions.assertEquals(newValue, newResult.intValue());
    }

    @Test
    void testPOSTCommandWithException()
    {
        //Arrange

        //Act
        var response = Unirest.post(restPath + "throwExceptionTest")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asJson();
        JsonObject error = response.mapError(JsonObject.class);

        //Assert
        Assertions.assertThrows(SimpleApplicationService.SimpleApplicationException.class, () -> {
            if ( error != null )
            {
                if (SimpleApplicationService.SimpleApplicationException.class.getName().equals(error.get("ExceptionType").getAsString()) )
                {
                    throw new Gson().fromJson(error.get("Exception").getAsString(), SimpleApplicationService.SimpleApplicationException.class ) ;
                }
            }

        });
    }

}
