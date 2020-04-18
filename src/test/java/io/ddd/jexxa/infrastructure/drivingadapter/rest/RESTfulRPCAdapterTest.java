package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.ddd.jexxa.application.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@SuppressWarnings("SameParameterValue")
@Execution(ExecutionMode.SAME_THREAD)
public class RESTfulRPCAdapterTest
{
    final int defaultPort = 7000;
    final String defaultHost = "localhost";
    Properties properties;
    final String restPath = "http://localhost:7000/SimpleApplicationService/";

    final int defaultValue = 42;
    final SimpleApplicationService simpleApplicationService = new SimpleApplicationService();

    RESTfulRPCAdapter objectUnderTest;

    @BeforeEach
    public void setupTests(){
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
    public void tearDownTests(){
        //tear down
        objectUnderTest.stop();
        objectUnderTest = null;
        Unirest.shutDown();
    }


    @Test // RPC call test: int getSimpleValue()
    public void testGETCommand() 
    {
        //Arrange -> Nothing TODO 

        //Act
        Integer result = Unirest.get(restPath + "getSimpleValue")
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();


        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        Assertions.assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );
    }

    @Test
    public void testWithRandomPort() 
    {
        //Arrange
        var secondAdapter = new RESTfulRPCAdapter("localhost",0);
        secondAdapter.register(simpleApplicationService);
        secondAdapter.start();
        var secondRestPath = "http://localhost:" + secondAdapter.getPort() + "/SimpleApplicationService/";


        //Act using secondAdapter 
        Integer result = Unirest.get(secondRestPath + "getSimpleValue")
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();


        secondAdapter.stop();

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        Assertions.assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );

    }

    @Test  // RPC call test: void setSimpleValue(44)
    public void testPOSTCommandWithOneAttribute()
    {
        //Arrange
        var newValue = 44;

        //Act
        var response = Unirest.post(restPath + "setSimpleValue")
                .header("Content-Type", "application/json")
                .body(newValue)
                .asJson();

        //Assert
        Integer newResult = Unirest.get(restPath + "getSimpleValue")
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(newValue, simpleApplicationService.getSimpleValue());
        Assertions.assertEquals(newValue, newResult.intValue());
    }

    @Test // RPC call test: void setSimpleValueObject(SimpleValueObject(44))
    public void testPOSTCommandWithOneObject()
    {
        //Arrange
        var newValue = new JexxaValueObject(44);

        //Act
        var response = Unirest.post(restPath + "setSimpleValueObject")
                .header("Content-Type", "application/json")
                .body(newValue)
                .asJson();

        //Assert
        Integer newResult = Unirest.get(restPath + "getSimpleValue")
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(newValue.getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        Assertions.assertEquals(newValue.getValue(), newResult.intValue());
    }

    @Test // RPC call test: void setSimpleValueObjectTwice(SimpleValueObject(44), SimpleValueObject(88))
    public void testPOSTCommandWithTwoObjects()
    {
        //Arrange
        var paramList = new JexxaValueObject[]{new JexxaValueObject(44), new JexxaValueObject(88)};

        //Act
        var response = Unirest.post(restPath + "setSimpleValueObjectTwice")
                .header("Content-Type", "application/json")
                .body(paramList)
                .asEmpty();

        //Assert
        Integer newResult = Unirest.get(restPath + "getSimpleValue")
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(paramList[1].getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        Assertions.assertEquals(paramList[1].getValue(), newResult.intValue());
    }

    @Test // RPC call test:  int setGetSimpleValue(44)
    public void testPOSTCommandWithReturnValue() 
    {
        //Arrange
        var newValue = 44;

        //Act
        var oldvalue = Unirest.post(restPath + "setGetSimpleValue")
                .header("Content-Type", "application/json")
                .body(newValue)
                .asObject(Integer.class).getBody();


        //Act
        //Assert
        Integer newResult = Unirest.get(restPath + "getSimpleValue")
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();

        //Assert
        Assertions.assertNotNull(oldvalue);
        Assertions.assertEquals(defaultValue, oldvalue.intValue());
        Assertions.assertEquals(newValue, simpleApplicationService.getSimpleValueObject().getValue());
        Assertions.assertEquals(newValue, newResult.intValue());
    }

    @Test
    public void testPOSTCommandWithException() 
    {
        //Arrange

        //Act
        var response = Unirest.post(restPath + "throwExceptionTest")
                .header("Content-Type", "application/json")
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
