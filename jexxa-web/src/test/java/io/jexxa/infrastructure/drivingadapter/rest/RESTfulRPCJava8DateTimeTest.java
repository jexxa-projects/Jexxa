package io.jexxa.infrastructure.drivingadapter.rest;

import static io.jexxa.infrastructure.drivingadapter.rest.RESTConstants.APPLICATION_TYPE;
import static io.jexxa.infrastructure.drivingadapter.rest.RESTConstants.CONTENT_TYPE;
import static io.jexxa.infrastructure.drivingadapter.rest.RESTfulRPCAdapter.HTTP_PORT_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.Properties;

import io.jexxa.application.applicationservice.Java8DateTimeApplicationService;
import io.jexxa.utils.json.JSONManager;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RESTfulRPCJava8DateTimeTest
{
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

        Unirest.config().setObjectMapper(new ObjectMapper()
        {

            @Override
            public <T> T readValue(String value, Class<T> valueType)
            {
                return JSONManager.getJSONConverter().fromJson(value, valueType);
            }

            @Override
            public String writeValue(Object value)
            {
                return JSONManager.getJSONConverter().toJson(value);
            }
        });
    }

    @Test
    void testGson()
    {
        LocalDate now = LocalDate.now();
        String json = JSONManager.getJSONConverter().toJson(now);

        LocalDate recreated = JSONManager.getJSONConverter().fromJson(json, LocalDate.class);
        assertEquals(now, recreated);
        System.out.println("SUCCESS");
    }


    @AfterEach
    void tearDownTests(){
        //tear down
        objectUnderTest.stop();
        objectUnderTest = null;
        Unirest.shutDown();
    }

    @Test
    void testLocalDateAsString() //OK
    {

        //Arrange
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
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(localDate, result);
    }

    @Test
    void testLocalDate()   //NOT OK
    {
        //Arrange
        LocalDate localDate = LocalDate.now();

        //Act
        var response = Unirest.post(REST_PATH + "setLocalDate")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(localDate.toString() )
                .asEmpty();

        LocalDate result = Unirest.get(REST_PATH + "getLocalDate")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(LocalDate.class).getBody();

        //Assert
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(localDate, result);
    }

    @Test
    void testLocalDateTime()
    {
        //Arrange
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
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(localDateTime, result);
    }

    @Test
    void testLocalTime()  //NOT OK
    {
        //Arrange
        LocalTime localTime = LocalTime.now();

        System.out.println( JSONManager.getJSONConverter().toJson(localTime) );

        //Act
        var response = Unirest.post(REST_PATH + "setLocalTime")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(localTime)
                .asEmpty();

        LocalTime result = Unirest.get(REST_PATH + "getLocalTime")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(LocalTime.class).getBody();

        //Assert
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(localTime, result);
    }

    @Test
    void testZonedDateTime()
    {
        //Arrange
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
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(zonedDateTime, result);
    }

    @Test
    void testDuration()  //NOT OK
    {
        //Arrange
        Duration duration = Duration.ofDays(2);
        System.out.println( JSONManager.getJSONConverter().toJson(duration));

        //Act
        var response = Unirest.post(REST_PATH + "setDuration")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(duration)
                .asEmpty();

        var result = Unirest.get(REST_PATH + "getDuration")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Duration.class).getBody();

        //Assert
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(duration, result);
    }

    @Test
    void testPeriod()     //NOT OK
    {
        //Arrange
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
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(period, result);
    }

    @Test
    void testInstant()   //NOT OK
    {
        //Arrange
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
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        assertEquals(instant, result);
    }

    @Test
    void testJava8DateTimeWrapper()   //NOT OK
    {
        //Arrange
        var java8DateTimeWrapper = new Java8DateTimeApplicationService.Java8DateTimeWrapper( LocalTime.now()
                , LocalDate.now()
                , LocalDateTime.now()
                , ZonedDateTime.now().withFixedOffsetZone()
                , Period.of(1, 0, 0)
                , Duration.ofDays(1)
                , Instant.now()
        );
        //Act
        var response = Unirest.post(REST_PATH + "setJava8DateTimeWrapper")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .body(java8DateTimeWrapper)
                .asEmpty();

        var result = Unirest.get(REST_PATH + "getJava8DateTimeWrapper")
                .header(CONTENT_TYPE, APPLICATION_TYPE)
                .asObject(Java8DateTimeApplicationService.Java8DateTimeWrapper.class).getBody();

        //Assert
        Assertions.assertTrue(response.isSuccess());
        assertNotNull(result);
        Assertions.assertEquals(java8DateTimeWrapper, result);
    }

}
