package io.jexxa.infrastructure.drivingadapter.rest;

import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.HTTP_PORT_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Properties;

import com.google.gson.Gson;
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

    @Test
    void testLocalDate()
    {
        //Arrange -> Nothing to do
        LocalDate localDate = LocalDate.now();
        Gson gson = new Gson();

        //Act

        var response = Unirest.post(REST_PATH + "setLocalDate")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(gson.toJson( localDate ))
                .asEmpty();

        LocalDate result = Unirest.get(REST_PATH + "getLocalDate")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(LocalDate.class).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(localDate, result);
    }

    @Test
    void testLocalDateTime()
    {
        //Arrange -> Nothing to do
        LocalDateTime localDateTime = LocalDateTime.now();

        //Act

        var response = Unirest.post(REST_PATH + "setLocalDateTime")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(localDateTime)
                .asEmpty();

        LocalDateTime result = Unirest.get(REST_PATH + "getLocalDateTime")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(LocalDateTime.class).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(localDateTime, result);
    }

    @Test
    void testLocalTime()
    {
        //Arrange -> Nothing to do
        LocalTime localTime = LocalTime.now();

        //Act

        var response = Unirest.post(REST_PATH + "setLocalTime")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(localTime)
                .asEmpty();

        LocalTime result = Unirest.get(REST_PATH + "getLocalTime")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(LocalTime.class).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(localTime, result);
    }

    @Test
    void testZonedDateTime()
    {
        //Arrange -> Nothing to do
        ZonedDateTime zonedDateTime = ZonedDateTime.now().withFixedOffsetZone();

        //Act
        var response = Unirest.post(REST_PATH + "setZonedDateTime")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(zonedDateTime)
                .asEmpty();

        var result = Unirest.get(REST_PATH + "getZonedDateTime")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(ZonedDateTime.class).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(zonedDateTime, result);
    }

    @Test
    void testDuration()
    {
        //Arrange -> Nothing to do
        Duration duration = Duration.ofDays(2);

        //Act
        var response = Unirest.post(REST_PATH + "setDuration")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(duration)
                .asEmpty();

        var result = Unirest.get(REST_PATH + "getDuration")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Duration.class).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(duration, result);
    }

    @Test
    void testPeriod()
    {
        //Arrange -> Nothing to do
        Period period = Period.ofDays(1);

        //Act
        var response = Unirest.post(REST_PATH + "setPeriod")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(period)
                .asEmpty();

        var result = Unirest.get(REST_PATH + "getPeriod")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Period.class).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(period, result);
    }

    @Test
    void testInstant()
    {
        //Arrange -> Nothing to do
        Instant instant = Instant.now();

        //Act
        var response = Unirest.post(REST_PATH + "setInstant")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(instant)
                .asEmpty();

        var result = Unirest.get(REST_PATH + "getInstant")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Instant.class).getBody();

        //Assert
        assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(instant, result);
    }
}
