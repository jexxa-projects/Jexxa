package io.jexxa.common.wrapper.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.jexxa.common.facade.json.JSONConverter;
import io.jexxa.common.facade.json.JSONManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JSonDateTimeTest
{
    private final JSONConverter objectUnderTest = JSONManager.getJSONConverter();

    @Test
    void testLocalDate()
    {
        // arrange
        var localDate = LocalDate.now();
        var expectedResult = "\"" + localDate + "\"";

        // act
        var resultToJson = objectUnderTest.toJson(localDate);
        var resultFromJson = objectUnderTest.fromJson(resultToJson, LocalDate.class);

        // assert
        assertEquals(expectedResult, resultToJson);
        assertEquals(localDate, resultFromJson);
    }

    @Test
    void testLocalDateAsJsonObject()
    {
        // arrange
        var localDate = LocalDate.now();
        var localDateAsJSonObject = new JsonObject();
        localDateAsJSonObject.add("year", new JsonPrimitive( localDate.getYear()));
        localDateAsJSonObject.add("month", new JsonPrimitive(localDate.getMonthValue() ));
        localDateAsJSonObject.add("day", new JsonPrimitive(localDate.getDayOfMonth() ));

        // act
        var resultFromJson = objectUnderTest.fromJson(localDateAsJSonObject.toString(), LocalDate.class);

        // assert
        assertEquals(localDate, resultFromJson);
    }

    @Test
    void testLocalDateTime()
    {
        // arrange
        var localDateTime = LocalDateTime.now();
        var expectedResult = "\"" + localDateTime + "\"";

        // act
        var resultToJson = objectUnderTest.toJson(localDateTime);
        var resultFromJson = objectUnderTest.fromJson(resultToJson, LocalDateTime.class);

        // assert
        assertEquals(expectedResult, resultToJson);
        assertEquals(localDateTime, resultFromJson);
    }

    @Test
    void testZonedDateTime()
    {
        // arrange
        var zonedDateTime = ZonedDateTime.now().withFixedOffsetZone();
        var expectedResult = "\"" + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(zonedDateTime) + "\"";

        // act
        var resultToJson = objectUnderTest.toJson(zonedDateTime);
        var resultFromJson = objectUnderTest.fromJson(resultToJson, ZonedDateTime.class);

        // assert
        assertEquals(expectedResult, resultToJson);
        assertEquals(zonedDateTime, resultFromJson);
    }

    @Test
    void testLocalTime()
    {
        // arrange
        var localTime = LocalTime.now();
        var expectedResult = new JsonObject();
        expectedResult.add("hour", new JsonPrimitive( localTime.getHour()));
        expectedResult.add("minute", new JsonPrimitive(localTime.getMinute() ));
        expectedResult.add("second", new JsonPrimitive(localTime.getSecond() ));
        expectedResult.add("nano", new JsonPrimitive(localTime.getNano() ));

        // act
        var resultToJson = objectUnderTest.toJson(localTime);
        var resultFromJson = objectUnderTest.fromJson(resultToJson, LocalTime.class);

        // assert
        assertEquals(expectedResult.toString(), resultToJson);
        assertEquals(localTime, resultFromJson);
    }


    @Test
    void testDuration()
    {
        // arrange
        var duration = Duration.of(1050000, ChronoUnit.NANOS);
        var expectedResult = new JsonObject();
        expectedResult.add("seconds", new JsonPrimitive(duration.getSeconds() ));
        expectedResult.add("nanos", new JsonPrimitive(duration.getNano() ));

        // act
        var resultToJson = objectUnderTest.toJson(duration);
        var resultFromJson = objectUnderTest.fromJson(resultToJson, Duration.class);

        // assert
        assertEquals(expectedResult.toString(), resultToJson);
        assertEquals(duration, resultFromJson);
    }

    @Test
    void testInstant()
    {
        // arrange
        var instant = Instant.now();
        var expectedResult = new JsonObject();
        expectedResult.add("seconds", new JsonPrimitive(instant.getEpochSecond() ));
        expectedResult.add("nanos", new JsonPrimitive(instant.getNano() ));

        // act
        var resultToJson = objectUnderTest.toJson(instant);
        var resultFromJson = objectUnderTest.fromJson(resultToJson, Instant.class);

        // assert
        assertEquals(expectedResult.toString(), resultToJson);
        assertEquals(instant, resultFromJson);
    }

    @Test
    void testPeriod()
    {
        // arrange
        var period = Period.of(1, 2, 3);
        var expectedResult = new JsonObject();
        expectedResult.add("years", new JsonPrimitive(period.getYears() ));
        expectedResult.add("months", new JsonPrimitive(period.getMonths() ));
        expectedResult.add("days", new JsonPrimitive(period.getDays() ));

        // act
        var resultToJson = objectUnderTest.toJson(period);
        var resultFromJson = objectUnderTest.fromJson(resultToJson, Period.class);

        // assert
        assertEquals(expectedResult.toString(), resultToJson);
        assertEquals(period, resultFromJson);
    }

}
