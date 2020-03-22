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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RESTfulRPCMethodAdapterTest
{
    int defaultPort = 7000;
    String defaultHost = "localhost";
    int defaultValue = 42;
    SimpleApplicationService simpleApplicationService = new SimpleApplicationService(defaultValue);
    RESTfulRPCModel resTfulRPCModel = new RESTfulRPCModel(simpleApplicationService);

    RESTfulRPCAdapter objectUnderTest;

    @Before
    public void setupTests(){
        //Setup
        objectUnderTest = new RESTfulRPCAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();
    }

    @After
    public void tearDownTests(){
        //tear down
        objectUnderTest.stop();
        objectUnderTest = null;
    }


    @Test // RPC call test: int getSimpleValue()
    public void testGETCommand() throws IOException
    {
        //Arrange
        var restPath = resTfulRPCModel.getGETCommand("getSimpleValue");
        assertTrue(restPath.isPresent());

        //Act
        String result = sendGETCommand(restPath.get());

        //Assert
        assertNotNull(result);
        assertEquals(defaultValue, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(simpleApplicationService.getSimpleValue()), result );
    }


    @Test  // RPC call test: void setSimpleValue(44)
    public void testPOSTCommandWithOneAttribute() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var newValue = 44;
        var restPath = resTfulRPCModel.getPOSTCommand("setSimpleValue");
        var responsePath = resTfulRPCModel.getGETCommand("getSimpleValue");
        assertTrue(restPath.isPresent());
        assertTrue(responsePath.isPresent());
        
        //Act
        sendPOSTCommand(restPath.get(), newValue);

        //Assert
        assertEquals(newValue, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(newValue), sendGETCommand(responsePath.get()));
    }

    @Test // RPC call test: void setSimpleValueObject(SimpleValueObject(44))
    public void testPOSTCommandWithOneObject() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var newValue = new SimpleValueObject(44);
        var restPath = resTfulRPCModel.getPOSTCommand("setSimpleValueObject");
        var responsePath = resTfulRPCModel.getGETCommand("getSimpleValue");
        assertTrue(responsePath.isPresent());
        assertTrue(restPath.isPresent());

        //Act
        sendPOSTCommand(restPath.get(), newValue);

        //Assert
        assertEquals(newValue.getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(Integer.toString(newValue.getValue()), sendGETCommand(responsePath.get()));
    }

    @Test // RPC call test: void setSimpleValueObjectTwice(SimpleValueObject(44), SimpleValueObject(88))
    public void testPOSTCommandWithTwoObjects() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var paramList = new SimpleValueObject[]{new SimpleValueObject(44), new SimpleValueObject(88)};
        var restPath = resTfulRPCModel.getPOSTCommand("setSimpleValueObjectTwice");
        var responsePath = resTfulRPCModel.getGETCommand("getSimpleValue");
        assertTrue(responsePath.isPresent());
        assertTrue(restPath.isPresent());

        //Act
        String returnValue = sendPOSTCommand(restPath.get(), paramList);

        //Assert
        assertNull(returnValue);
        assertEquals(paramList[1].getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(Integer.toString(paramList[1].getValue()), sendGETCommand(responsePath.get()));
    }

    @Test // RPC call test:  int setGetSimpleValue(44)
    public void testPOSTCommandWithReturnValue() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var newValue = 44;
        var restPath = resTfulRPCModel.getPOSTCommand("setGetSimpleValue");
        var responsePath = resTfulRPCModel.getGETCommand("getSimpleValue");
        assertTrue(responsePath.isPresent());
        assertTrue(restPath.isPresent());

        //Act
        String returnValue = sendPOSTCommand(restPath.get(), newValue);

        //Assert
        assertNotNull(returnValue);
        assertEquals(Integer.toString(defaultValue), returnValue);
        assertEquals(newValue, simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(Integer.toString(newValue), sendGETCommand(responsePath.get()));
    }

    @Test(expected = SimpleApplicationService.SimpleApplicationException.class) // RPC call test:  void throwExceptionTest()
    public void testPOSTCommandWithException() throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        //Arrange
        var restPath = resTfulRPCModel.getPOSTCommand("throwExceptionTest");
        assertTrue(restPath.isPresent());

        //Act
        sendPOSTCommand(restPath.get());
    }

    private  String sendGETCommand(RESTfulRPCModel.RESTfulRPCMethod restPath) throws IOException
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


    private String sendPOSTCommand(RESTfulRPCModel.RESTfulRPCMethod restPath) throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        return sendPOSTCommand(restPath, "");
    }

    private String sendPOSTCommand(RESTfulRPCModel.RESTfulRPCMethod restPath, Object parameter) throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        final Gson gson = new Gson();
        return sendPOSTCommand(restPath, gson.toJson(parameter));
    }

    private String sendPOSTCommand(RESTfulRPCModel.RESTfulRPCMethod restPath, Object[] parameterList) throws IOException, SimpleApplicationService.SimpleApplicationException
    {
        final Gson gson = new Gson();
        return sendPOSTCommand(restPath, gson.toJson(parameterList));
    }


    private String sendPOSTCommand(RESTfulRPCModel.RESTfulRPCMethod restfulRPCMethod, String value) throws IOException, SimpleApplicationService.SimpleApplicationException
    {

        URL url = new URL("http://" + defaultHost + ":" + defaultPort + restfulRPCMethod.getResourcePath());
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
