package io.jexxa.infrastructure.drivingadapter.rest;

import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.HTTP_PORT_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Properties;

import io.jexxa.application.applicationservice.Java8DateTimeApplicationService;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RESTfulRPCJava8DateTimeTest
{
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_TYPE = "application/json";

    private static final String REST_PATH = "http://localhost:7000/Java8DateTimeApplicationService/";

    private final Java8DateTimeApplicationService java8DateTimeApplicationService = new Java8DateTimeApplicationService();

    private RESTfulRPCAdapter objectUnderTest;

    @BeforeEach
    void setupTests(){
        //Setup
        var properties = new Properties();
        var defaultHost = "localhost";
        var defaultPort = 7000;

        properties.put(RESTfulRPCAdapter.HOST_PROPERTY, defaultHost);
        properties.put(HTTP_PORT_PROPERTY, Integer.toString(defaultPort));

        objectUnderTest = RESTfulRPCAdapter.createAdapter(properties);
        objectUnderTest.register(java8DateTimeApplicationService);
        objectUnderTest.start();
    }

    @AfterEach
    void tearDownTests(){
        //tear down
        objectUnderTest.stop();
        objectUnderTest = null;
        Unirest.shutDown();
    }

    @Test
    void testLocalDateAsString()
    {

        //Arrange -> Nothing to do
        LocalDate localDate = LocalDate.now();

        //Act

        var response = Unirest.post(REST_PATH + "setLocalDate")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(localDate)
                .asEmpty();

        LocalDate result = Unirest.get(REST_PATH + "getLocalDate")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(LocalDate.class).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(localDate, result);
    }
}
