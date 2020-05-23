package io.jexxa.core;


import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;

import io.jexxa.TestConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class BoundedContextTest
{
    private final JexxaMain jexxaMain = new JexxaMain("BoundedContextTest");
    private BoundedContext objectUnderTest;

    @Test
    @Timeout(1)
    protected void shutdown()
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
        assertTimeout(Duration.ofSeconds(1), (Executable) thread::join);
    }

    protected void waitForShutDown()
    {
        objectUnderTest = jexxaMain.start();
        objectUnderTest.waitForShutdown();
    }
}
