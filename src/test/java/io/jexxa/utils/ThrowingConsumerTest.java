package io.jexxa.utils;

import static io.jexxa.TestTags.UNIT_TEST;
import static io.jexxa.utils.ThrowingConsumer.exceptionCollector;
import static io.jexxa.utils.ThrowingConsumer.exceptionLogger;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(UNIT_TEST)
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
                        Assertions.assertDoesNotThrow(() -> exceptionLogger(value -> Integer.divideUnsigned(value, 0)))
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