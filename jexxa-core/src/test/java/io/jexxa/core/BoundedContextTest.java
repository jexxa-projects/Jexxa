package io.jexxa.core;


import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import io.jexxa.TestConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
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
    void shutdown()
    {
        //Arrange
        var thread = new Thread(this::waitForShutDown);
        thread.start();

        await().atMost(1, TimeUnit.SECONDS).until(() -> (objectUnderTest != null && objectUnderTest.isRunning()));

        //Act
        objectUnderTest.shutdown();
        assertTimeout(Duration.ofSeconds(1), (Executable) thread::join);
    }

    void waitForShutDown()
    {
        objectUnderTest = jexxaMain.start();
        objectUnderTest.waitForShutdown();
    }
}
