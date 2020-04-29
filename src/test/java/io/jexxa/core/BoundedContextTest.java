package io.jexxa.core;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag("unit-test")
public class BoundedContextTest
{
    final JexxaMain jexxaMain = new JexxaMain("BoundedContextTest");
    BoundedContext objectUnderTest;

    @Test
    @Timeout(1)
    public void shutdown()
    {
        //Arrange
        var thread = new Thread(this::waitForShutDown);
        thread.start();

        while ( objectUnderTest == null ||
                !objectUnderTest.isRunning())
        {
            Thread.onSpinWait();
        }

        //Act
        objectUnderTest.shutdown();
        Assertions.assertTimeout(Duration.ofSeconds(1), (Executable) thread::join);
    }

    void waitForShutDown()
    {
        objectUnderTest = jexxaMain.start();
        objectUnderTest.waitForShutdown();
    }
}
