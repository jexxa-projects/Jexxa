package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;
import org.junit.Test;

public class JavalinRESTTest
{
    @Test
    public void startRESTServer() throws IOException
    {
        SimpleApplicationService simpleApplicationService = new SimpleApplicationService(42);

        JavalinREST javalinREST = new JavalinREST();
        javalinREST.register(simpleApplicationService);

        javalinREST.start();
        
        assertTrue(sendGETCommand().contains("42"));
        
    }



    public  String sendGETCommand( ) throws IOException
    {

        URL url = new URL("http://localhost:7000/SimpleApplicationService/getSimpleValue");
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
