package io.ddd.jexxa.core;


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

import io.ddd.jexxa.application.annotation.ApplicationService;
import io.ddd.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;
import io.ddd.jexxa.application.applicationservice.JexxaApplicationService;
import io.ddd.jexxa.application.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.application.domainservice.InitializeJexxaAggregates;
import io.ddd.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
public class JexxaMainTest
{
    private Properties properties;
    private JexxaMain objectUnderTest;
    private final String packageName = "io.ddd.jexxa";

    @BeforeEach
    public void initTests()
    {
        properties = new Properties();
        properties.putAll(getRESTfulRPCProperties());
    }

    @AfterEach
    public void tearDownTests()
    {
        if (objectUnderTest != null)
        {
            objectUnderTest.stopDrivingAdapters();
        }
    }


    
    @Test
    public void bindToPort()
    {
        //Arrange
        objectUnderTest = new JexxaMain("HelloJexxa", properties);
        objectUnderTest.whiteList(packageName);


        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest.bindToPort(JMXAdapter.class, SimpleApplicationService.class);
        objectUnderTest.bindToPort(RESTfulRPCAdapter.class, SimpleApplicationService.class);

        objectUnderTest.startDrivingAdapters();


        //Assert
        assertJMXAdapter(SimpleApplicationService.class);
        assertRESTfulRPCAdapter();
    }

    @Test
    public void bindToPortWithDrivenAdapter()
    {
        //Arrange
        objectUnderTest = new JexxaMain("HelloJexxa", properties);
        objectUnderTest.whiteList(packageName);


        //Act: Bind a concrete type of DrivingAdapter to a concrete type of port
        objectUnderTest.bindToPort(JMXAdapter.class, ApplicationServiceWithDrivenAdapters.class);
        objectUnderTest.bindToPort(RESTfulRPCAdapter.class, ApplicationServiceWithDrivenAdapters.class);

        objectUnderTest.startDrivingAdapters();


        //Assert
        assertJMXAdapter(ApplicationServiceWithDrivenAdapters.class);
    }
    

    @Test
    public void bindToAnnotatedPorts()
    {
        //Arrange
        objectUnderTest = new JexxaMain("HelloJexxa", properties);
        objectUnderTest.whiteList(packageName);


        //Act: Bind all DrivingAdapter to all ApplicationServices
        objectUnderTest.bindToAnnotatedPorts(RESTfulRPCAdapter.class, ApplicationService.class);
        objectUnderTest.startDrivingAdapters();

        //Assert
        assertRESTfulRPCAdapter();
    }


    @Test
    public void bootstrapService()
    {
        //Arrange
        objectUnderTest = new JexxaMain("HelloJexxa", properties);
        objectUnderTest.whiteList(packageName);

        //Act
        objectUnderTest.addBootstrapService(
                InitializeJexxaAggregates.class,
                InitializeJexxaAggregates::initDomainData);

        var jexxaApplicationService = objectUnderTest.getInstanceOfPort(JexxaApplicationService.class);

        //Assert 
        Assertions.assertTrue(jexxaApplicationService.getAggregateCount() > 0);
    }


    /* ---------------------Util methods ------------------ */
    void assertJMXAdapter(Class<?> clazz) {
        //Assert
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> result = mbs.queryMBeans(null , null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.
                stream().
                anyMatch(element -> element.getClassName().endsWith(clazz.getSimpleName()))
        );
    }

    void assertRESTfulRPCAdapter() {
        String result = sendGETCommand(SimpleApplicationService.class.getSimpleName()+ "/getSimpleValue");
        Assertions.assertNotNull(result);
        Assertions.assertEquals(Integer.toString(42), result);
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

            try ( BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream()))) )
            {

                String output = br.readLine();

                conn.disconnect();

                return output;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
