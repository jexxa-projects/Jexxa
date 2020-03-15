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
import org.junit.Test;

public class JavalinAdapterTest
{
    @Test
    public void testJavalinGETCommand() throws IOException
    {
        //Arrange
        var defaultPort = 7000;
        var defaultHost = "localhost";
        var simpleApplicationService = new SimpleApplicationService(42);
        var objectUnderTest = new JavalinAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        //Act
        String result =sendGETCommand(defaultHost, defaultPort);

        //Assert
        assertEquals(42, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(simpleApplicationService.getSimpleValue()), result );

        objectUnderTest.stop();
    }


    @Test
    public void testJavalinPOSTCommand() throws IOException
    {
        //Arrange
        var defaultPort = 7000;
        var defaultHost = "localhost";
        var defaultValue = 42;
        var newValue = 44;
        var simpleApplicationService = new SimpleApplicationService(defaultValue);
        var objectUnderTest = new JavalinAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();

        //Act
        sendPOSTCommand(defaultHost, defaultPort, Integer.toString(newValue));

        //Assert
        assertEquals(newValue, simpleApplicationService.getSimpleValue());
        assertEquals(Integer.toString(newValue), sendGETCommand(defaultHost, defaultPort));

        objectUnderTest.stop();
    }


    public  String sendGETCommand(String defaultHost, int defaultPort) throws IOException
    {

        URL url = new URL("http://" + defaultHost + ":" + defaultPort + "/SimpleApplicationService/getSimpleValue");
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

    public  String sendPOSTCommand(String defaultHost, int defaultPort, String value) throws IOException
    {

        URL url = new URL("http://" + defaultHost + ":" + defaultPort + "/SimpleApplicationService/setSimpleValue");
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

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output = br.readLine();

        conn.disconnect();

        return output;
    }
}
