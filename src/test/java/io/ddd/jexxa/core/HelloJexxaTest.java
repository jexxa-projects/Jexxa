package io.ddd.jexxa.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import io.ddd.jexxa.infrastructure.stereotype.DrivingAdapter;
import io.ddd.stereotype.applicationcore.ApplicationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HelloJexxaTest
{
    private Properties properties;
    private Jexxa objectUnderTest;

    @Before
    public void initTests()
    {
        properties = new Properties();
        setJMXProperties();
        properties.putAll(getJMXProperties());
        properties.putAll(getRESTfulRPCProperties());
    }

    @After
    public void tearDownTests()
    {
        if (objectUnderTest != null)
        {
            objectUnderTest.stopDrivingAdapters();
        }
    }


    
    @Test
    public void simpleHelloJexxa()
    {
        //Arrange
        objectUnderTest = new Jexxa(properties);

        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest.bind(JMXAdapter.class, SimpleApplicationService.class);
        objectUnderTest.bind(RESTfulRPCAdapter.class, SimpleApplicationService.class);

        objectUnderTest.startDrivingAdapters();


        //Assert
        assertJMXAdapter();
        assertRESTfulRPCAdapter();
    }

    @Test
    public void simpleHelloJexxaAnnotation()
    {
        //Arrange
        objectUnderTest = new Jexxa(properties);

        //Act: Bind all DrivingAdapter to all ApplicationServices
        objectUnderTest.bindByAnnotation(DrivingAdapter.class, ApplicationService.class);
        objectUnderTest.startDrivingAdapters();
        
        //Assert
        assertJMXAdapter();
        assertRESTfulRPCAdapter();
    }

    @Test
    public void simpleHelloJexxaClassAnnotatedPorts()
    {
        //Arrange
        objectUnderTest = new Jexxa(properties);

        //Act: Bind all DrivingAdapter to all ApplicationServices
        objectUnderTest.bindToAnnotatedPorts(RESTfulRPCAdapter.class, ApplicationService.class);
        objectUnderTest.startDrivingAdapters();

        //Assert
        assertRESTfulRPCAdapter();
    }



    void assertJMXAdapter() {
        //Assert
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> result = mbs.queryMBeans(null , null);

        assertNotNull(result);
        assertTrue(result.
                stream().
                anyMatch(element -> element.getClassName().endsWith(SimpleApplicationService.class.getSimpleName()))
        );
    }

    void assertRESTfulRPCAdapter() {
        String result = sendGETCommand(SimpleApplicationService.class.getSimpleName()+ "/getSimpleValue");
        assertNotNull(result);
        assertEquals(Integer.toString(42), result);
    }

    void setJMXProperties() {
        System.setProperty("com.sun.management.jmxremote.host", "localhost");
        System.setProperty("com.sun.management.jmxremote.port", "62345");
        System.setProperty("com.sun.management.jmxremote.rmi.port", "62345");
        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
        System.setProperty("com.sun.management.jmxremote.ssl", "false");
    }


    Properties getJMXProperties() {
        return System.getProperties();
    }

    Properties getRESTfulRPCProperties() {
        Properties properties = new Properties();
        properties.put(RESTfulRPCAdapter.HOST_PROPERTY, "localhost");
        properties.put(RESTfulRPCAdapter.PORT_PROPERTY, Integer.toString(7000));
        return properties;
    }

    private  String sendGETCommand(String restPath) 
    {
        try
        {
            URL url = new URL("http://"
                    + properties.get(RESTfulRPCAdapter.HOST_PROPERTY)
                    + ":"
                    + properties.get(RESTfulRPCAdapter.PORT_PROPERTY)
                    + "/"
                    + restPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                throw new IOException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output = br.readLine();

            conn.disconnect();

            return output;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
