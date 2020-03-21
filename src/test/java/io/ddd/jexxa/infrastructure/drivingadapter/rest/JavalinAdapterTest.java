package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.domain.valueobject.SimpleValueObject;
import org.junit.Test;

public class JavalinAdapterTest
{
    int defaultPort = 7000;
    String defaultHost = "localhost";
    SimpleApplicationService simpleApplicationService = new SimpleApplicationService(42);
    RESTfulHTTPGenerator restfullHTTPGenerater = new RESTfulHTTPGenerator(simpleApplicationService);

    @Test
    public void testJavalinGETCommand() throws IOException
    {
        //Arrange
        var objectUnderTest = new JavalinAdapter(defaultHost, defaultPort);

        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        //Act
        var restPath = restfullHTTPGenerater.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(restPath.isPresent());

        String result = sendGETCommand(restPath.get());

        //Assert
        assertEquals(42, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(simpleApplicationService.getSimpleValue()), result );

        objectUnderTest.stop();
    }

    @Test
    public void testJavalinGETCommandDefaultHostname() throws IOException
    {
        //Arrange
        var objectUnderTest = new JavalinAdapter(defaultPort);

        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        var restPath = restfullHTTPGenerater.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(restPath.isPresent());

        //Act
        String result =sendGETCommand(restPath.get());

        //Assert
        assertEquals(42, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(simpleApplicationService.getSimpleValue()), result );

        objectUnderTest.stop();
    }


    @Test
    public void testJavalinPOSTCommand() throws IOException
    {
        //Arrange
        var objectUnderTest = new JavalinAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        //Act
        var newValue = 44;

        var restPath = restfullHTTPGenerater.
                getPOSTCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("setSimpleValue")).
                findFirst();
        assertTrue(restPath.isPresent());


        sendPOSTCommand(restPath.get(), Integer.toString(newValue));

        //Assert
        var restfullHTTPGenerater = new RESTfulHTTPGenerator(simpleApplicationService);
        var responsePath = restfullHTTPGenerater.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(responsePath.isPresent());
        
        assertEquals(newValue, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(newValue), sendGETCommand(responsePath.get()));

        objectUnderTest.stop();
    }

    @Test
    public void testJavalinPOSTCommandValueObject() throws IOException
    {
        //Arrange
        var objectUnderTest = new JavalinAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        var newValue = new SimpleValueObject(44);

        //Act
        var restPath = restfullHTTPGenerater.
                getPOSTCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("setSimpleValueObject")).
                findFirst();
        assertTrue(restPath.isPresent());


        sendPOSTCommand(restPath.get(), JsonConverter.toJson(newValue));

        //Assert
        var restfullHTTPGenerater = new RESTfulHTTPGenerator(simpleApplicationService);
        var responsePath = restfullHTTPGenerater.
                getGETCommands().
                stream().
                filter(element -> element.getResourcePath().endsWith("getSimpleValue")).
                findFirst();
        assertTrue(responsePath.isPresent());

        assertEquals(newValue.getValue(), simpleApplicationService.getSimpleValueObject().getValue());
        assertEquals(Integer.toString(newValue.getValue()), sendGETCommand(responsePath.get()));

        objectUnderTest.stop();
    }

    private  String sendGETCommand(RESTfulHTTPGenerator.RESTfulHTTP restPath) throws IOException
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

    private void sendPOSTCommand(RESTfulHTTPGenerator.RESTfulHTTP restPath, String value) throws IOException
    {

        URL url = new URL("http://" + defaultHost + ":" + defaultPort + restPath.getResourcePath());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");

        try(OutputStream os = conn.getOutputStream()) {
            byte[] input = value.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
            throw new IOException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

     
        conn.disconnect();

    }
}
