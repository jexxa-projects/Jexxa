package io.jexxa.core;

import static io.jexxa.TestTags.UNIT_TEST;

import java.time.Duration;

import io.jexxa.TestTags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestTags.UNIT_TEST)
class BoundedContextTest
{
    final JexxaMain jexxaMain = new JexxaMain("BoundedContextTest");
    BoundedContext objectUnderTest;

    @Test
    @Timeout(1)
    void shutdown()
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
