package io.ddd.jexxa.utils;

import static io.ddd.jexxa.utils.ThrowingConsumer.exceptionCollector;
import static io.ddd.jexxa.utils.ThrowingConsumer.exceptionLogger;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ThrowingConsumerTest
{

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void exceptionLoggerTest()
    {
        //Arrange
        Integer[] values = {1,2,3};

        //Act
        Arrays.stream(values).
                forEach(
                        exceptionLogger(value -> Integer.divideUnsigned(value, 0))
                );

        //Assert => No assertion must occur
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void exceptionCollectorTest()
    {
        //Arrange
        Integer[] values = {1,2,3};
        var exceptions = new ArrayList<Throwable>();

        //Act
        Arrays.stream(values).
                forEach(
                        exceptionCollector(value -> Integer.divideUnsigned(value, 0),exceptions)
                );

        //Assert
        Assertions.assertFalse(exceptions.isEmpty());
        Assertions.assertEquals(values.length, exceptions.size());
    }
}