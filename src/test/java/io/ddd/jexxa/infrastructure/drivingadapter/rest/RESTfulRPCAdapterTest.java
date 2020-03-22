package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import io.ddd.jexxa.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.domain.valueobject.SimpleValueObject;
import org.junit.Test;

public class RESTfulRPCAdapterTest
{
    int defaultPort = 7000;
    String defaultHost = "localhost";
    int defaultValue = 42;
    SimpleApplicationService simpleApplicationService = new SimpleApplicationService(defaultValue);
    RESTfulRPCModel resTfulRPCModel = new RESTfulRPCModel(simpleApplicationService);


    @Test // RPC call test: int getSimpleValue()
    public void testGETCommand() throws IOException
    {
        //Arrange
        var objectUnderTest = new RESTfulRPCAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        //Act
        var restPath = resTfulRPCModel.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(restPath.isPresent());

        String result = sendGETCommand(restPath.get());

        //Assert
        assertNotNull(result);
        assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(simpleApplicationService.getSimpleValue()), result );

        objectUnderTest.stop();
    }

    @Test // RPC call test: int getSimpleValue()
    public void testGETCommandDefaultHostname() throws IOException
    {
        //Arrange
        var objectUnderTest = new RESTfulRPCAdapter(defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        var restPath = resTfulRPCModel.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(restPath.isPresent());

        //Act
        String result = sendGETCommand(restPath.get());

        //Assert
        assertNotNull(result);
        assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(simpleApplicationService.getSimpleValue()), result );

        objectUnderTest.stop();
    }


    @Test  // RPC call test: void setSimpleValue(44)
    public void testPOSTCommandWithOneAttribute() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var objectUnderTest = new RESTfulRPCAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        //Act
        var newValue = 44;

        var restPath = resTfulRPCModel.
                getPOSTCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("setSimpleValue")).
                findFirst();
        assertTrue(restPath.isPresent());


        sendPOSTCommand(restPath.get(), newValue);

        //Assert
        var responsePath = resTfulRPCModel.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(responsePath.isPresent());

        assertEquals(newValue, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(newValue), sendGETCommand(responsePath.get()));

        objectUnderTest.stop();
    }

    @Test // RPC call test: void setSimpleValueObject(SimpleValueObject(44))
    public void testPOSTCommandWithOneObject() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var objectUnderTest = new RESTfulRPCAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        var newValue = new SimpleValueObject(44);
        
        //Act
        var restPath = resTfulRPCModel.
                getPOSTCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("setSimpleValueObject")).
                findFirst();
        assertTrue(restPath.isPresent());


        sendPOSTCommand(restPath.get(), newValue);

        //Assert
        var responsePath = resTfulRPCModel.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(responsePath.isPresent());

        assertEquals(newValue.getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(Integer.toString(newValue.getValue()), sendGETCommand(responsePath.get()));

        objectUnderTest.stop();
    }

    @Test // RPC call test: void setSimpleValueObjectTwice(SimpleValueObject(44), SimpleValueObject(88))
    public void testPOSTCommandWithTwoObjects() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var objectUnderTest = new RESTfulRPCAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        SimpleValueObject[] paramList = {new SimpleValueObject(44), new SimpleValueObject(88)};

        //Act
        var restPath = resTfulRPCModel.
                getPOSTCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("setSimpleValueObjectTwice")).
                findFirst();
        assertTrue(restPath.isPresent());


        String returnValue = sendPOSTCommand(restPath.get(), paramList);

        //Assert
        var responsePath = resTfulRPCModel.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(responsePath.isPresent());

        assertNull(returnValue);
        assertEquals(paramList[1].getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(Integer.toString(paramList[1].getValue()), sendGETCommand(responsePath.get()));

        objectUnderTest.stop();
    }

    @Test // RPC call test:  int setGetSimpleValue(44)
    public void testPOSTCommandWithReturnValue() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var objectUnderTest = new RESTfulRPCAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        var newValue = 44;

        //Act
        var restPath = resTfulRPCModel.
                getPOSTCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("setGetSimpleValue")).
                findFirst();
        assertTrue(restPath.isPresent());


        String returnValue = sendPOSTCommand(restPath.get(), newValue);

        //Assert
        var responsePath = resTfulRPCModel.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(responsePath.isPresent());

        assertNotNull(returnValue);
        assertEquals(Integer.toString(defaultValue), returnValue);
        assertEquals(newValue, simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(Integer.toString(newValue), sendGETCommand(responsePath.get()));

        objectUnderTest.stop();
    }

    @Test(expected = SimpleApplicationService.SimpleApplicationException.class) // RPC call test:  void throwExceptionTest()
    public void testPOSTCommandWithException() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var objectUnderTest = new RESTfulRPCAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        //Act
        var restPath = resTfulRPCModel.
                getPOSTCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("throwExceptionTest")).
                findFirst();
        assertTrue(restPath.isPresent());
        
        try
        {
            sendPOSTCommand(restPath.get());
        } catch (SimpleApplicationService.SimpleApplicationException e) {
            objectUnderTest.stop();
            throw e;
        }
    }

    private  String sendGETCommand(RESTfulRPCModel.RESTfulRPC restPath) throws IOException
    {

        URL url = new URL("http://" + defaultHost + ":" + defaultPort + restPath.getResourcePath());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
            throw new IOException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output = br.readLine();

        conn.disconnect();

        return output;
    }


    private String sendPOSTCommand(RESTfulRPCModel.RESTfulRPC restPath) throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        return sendPOSTCommand(restPath, "");
    }

    private String sendPOSTCommand(RESTfulRPCModel.RESTfulRPC restPath, Object parameter) throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        final Gson gson = new Gson();
        return sendPOSTCommand(restPath, gson.toJson(parameter));
    }

    private String sendPOSTCommand(RESTfulRPCModel.RESTfulRPC restPath, Object[] parameterList) throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        final Gson gson = new Gson();
        return sendPOSTCommand(restPath, gson.toJson(parameterList));
    }


    private String sendPOSTCommand(RESTfulRPCModel.RESTfulRPC restfulRPC, String value) throws IOException, SimpleApplicationService.SimpleApplicationException
    {

        URL url = new URL("http://" + defaultHost + ":" + defaultPort + restfulRPC.getResourcePath());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = value.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
            // Try to recreate Exception
            if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_ACCEPTABLE) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getErrorStream())));

                String output = br.readLine();

                final Gson gson = new Gson();
                throw gson.fromJson(output, SimpleApplicationService.SimpleApplicationException.class);
            }

            throw new IOException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }


        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output = br.readLine();

        conn.disconnect();

        return output;
    }
}
