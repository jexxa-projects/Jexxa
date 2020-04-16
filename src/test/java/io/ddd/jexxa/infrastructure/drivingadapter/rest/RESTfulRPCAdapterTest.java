package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.ddd.jexxa.application.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import kong.unirest.Unirest;
import kong.unirest.UnirestParsingException;
import kong.unirest.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("SameParameterValue")
public class RESTfulRPCAdapterTest
{
    final int defaultPort = 7000;
    final String defaultHost = "localhost";
    Properties properties;
    final String restPath = "http://localhost:7000/SimpleApplicationService/";

    final int defaultValue = 42;
    final SimpleApplicationService simpleApplicationService = new SimpleApplicationService(defaultValue);

    RESTfulRPCAdapter objectUnderTest;

    @Before
    public void setupTests(){
        //Setup
        properties = new Properties();
        properties.put(RESTfulRPCAdapter.HOST_PROPERTY, defaultHost);
        properties.put(RESTfulRPCAdapter.PORT_PROPERTY, Integer.toString(defaultPort));

        objectUnderTest = new RESTfulRPCAdapter(properties);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();
    }

    @After
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
        assertNotNull(result);
        assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );
    }

    @Test
    public void testWithRandomPort() 
    {
        //Setup
        var secondAdapter = new RESTfulRPCAdapter("localhost",0);
        secondAdapter.register(simpleApplicationService);
        secondAdapter.start();

        //Arrange
        Integer result = Unirest.get(restPath + "getSimpleValue")
                .header("Content-Type", "application/json")
                .asObject(Integer.class).getBody();


        secondAdapter.stop();

        //Assert
        assertNotNull(result);
        assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        assertEquals(simpleApplicationService.getSimpleValue(), result.intValue() );

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

        assertTrue(response.isSuccess());
        assertEquals(newValue, simpleApplicationService.getSimpleValue());
        assertEquals(newValue, newResult.intValue());
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

        assertTrue(response.isSuccess());
        assertEquals(newValue.getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(newValue.getValue(), newResult.intValue());
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

        assertTrue(response.isSuccess());
        assertEquals(paramList[1].getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(paramList[1].getValue(), newResult.intValue());
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
        assertNotNull(oldvalue);
        assertEquals(defaultValue, oldvalue.intValue());
        assertEquals(newValue, simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(newValue, newResult.intValue());
    }

    @Test(expected = SimpleApplicationService.SimpleApplicationException.class) // RPC call test:  void throwExceptionTest()
    public void testPOSTCommandWithException() throws Throwable
    {
        //Arrange

        //Act
        var response = Unirest.post(restPath + "throwExceptionTest")
                .header("Content-Type", "application/json")
                .asJson();
        JsonObject error = response.mapError(JsonObject.class);

        //Assert
        if ( error != null )
        {
            if (SimpleApplicationService.SimpleApplicationException.class.getName().equals(error.get("ExceptionType").getAsString()) )
            {
                throw new Gson().fromJson(error.get("Exception").getAsString(), SimpleApplicationService.SimpleApplicationException.class ) ;
            }
        }
    }

}
