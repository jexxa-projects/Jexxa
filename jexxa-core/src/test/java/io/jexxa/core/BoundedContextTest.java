package io.jexxa.core;


import io.jexxa.TestConstants;
import io.jexxa.adapterapi.drivingadapter.HealthCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.UNIT_TEST)
class BoundedContextTest
{
    private final JexxaMain jexxaMain = new JexxaMain(BoundedContextTest.class);
    private BoundedContext objectUnderTest;


    @BeforeEach
    void init()
    {
        objectUnderTest = jexxaMain.getBoundedContext();
    }

    @Test
    void shutdown()
    {
        //Arrange
        var thread = new Thread(this::waitForShutDown);
        thread.start();

        await().atMost(1, TimeUnit.SECONDS).until(() -> (objectUnderTest != null && objectUnderTest.isRunning()));

        //Act
        objectUnderTest.stop();
        assertTimeout(Duration.ofSeconds(1), (Executable) thread::join);
    }

    @Test
    void testIsHealthy()
    {
        //Arrange
        objectUnderTest.registerHealthCheck(new SimpleHealthCheck(true));

        //Assert
        assertTrue(objectUnderTest.isHealthy());
        assertEquals(1, objectUnderTest.getDiagnostics().size());
        assertTrue(objectUnderTest.getDiagnostics().get(0).isHealthy());
    }

    @Test
    void testIsUnhealthy()
    {
        //Arrange
        objectUnderTest.registerHealthCheck(new SimpleHealthCheck(true));
        objectUnderTest.registerHealthCheck(new SimpleHealthCheck(false));

        //Assert
        assertFalse(objectUnderTest.isHealthy());
        assertEquals(2, objectUnderTest.getDiagnostics().size());
        assertTrue(objectUnderTest.getDiagnostics().get(0).isHealthy());
        assertFalse(objectUnderTest.getDiagnostics().get(1).isHealthy());
    }

    void waitForShutDown()
    {
        jexxaMain.start()
                .waitForShutdown()
                .stop();
    }

    public static class SimpleHealthCheck extends HealthCheck
    {
        private final boolean isHealthy;

        public SimpleHealthCheck( boolean isHealthy )
        {
            this.isHealthy = isHealthy;
        }

        @Override
        public boolean healthy()
        {
            return isHealthy;
        }

        @Override
        public String getStatusMessage() {
            return "";
        }
    }
}
