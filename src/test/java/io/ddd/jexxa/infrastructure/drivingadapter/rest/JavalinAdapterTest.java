package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;
import org.junit.Test;

public class JavalinAdapterTest
{
    @Test
    public void startRESTServer() throws IOException
    {
        int defaultPort = 7000;
        String defaultHost = "localhost";
        SimpleApplicationService simpleApplicationService = new SimpleApplicationService(42);


        var objectUnderTest = new JavalinAdapter(defaultHost, defaultPort);
        objectUnderTest.register(simpleApplicationService);
        objectUnderTest.start();
        
        assertTrue(sendGETCommand(defaultHost, defaultPort).contains("42"));
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
}
