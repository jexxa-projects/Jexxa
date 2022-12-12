package io.jexxa.core;


import io.jexxa.TestConstants;
import io.jexxa.adapterapi.drivingadapter.HealthCheck;
import io.jexxa.application.JexxaTestApplication;
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
    private JexxaMain jexxaMain;
    private BoundedContext objectUnderTest;


    @BeforeEach
    void init()
    {
        jexxaMain = new JexxaMain(JexxaTestApplication.class);

        objectUnderTest = jexxaMain
                .disableBanner()
                .getBoundedContext();
    }

    @Test
    void shutdown()
    {
        //Arrange
        var thread = new Thread(jexxaMain::run);
        thread.start();

        await().atMost(1, TimeUnit.SECONDS)
                .until(() -> (objectUnderTest != null && objectUnderTest.isRunning()));

        //Act
        jexxaMain.stop();

        //Assert
        assertTimeout(Duration.ofSeconds(1), (Executable) thread::join);
    }

    @Test
    void testIsHealthy()
    {
        //Arrange
        objectUnderTest.registerHealthCheck(new SimpleHealthCheck(true));

        //Assert
        assertTrue(objectUnderTest.isHealthy());
        assertEquals(1, objectUnderTest.diagnostics().size());
        assertTrue(objectUnderTest.diagnostics().get(0).isHealthy());
    }

    @Test
    void testIsUnhealthy()
    {
        //Arrange
        objectUnderTest.registerHealthCheck(new SimpleHealthCheck(true));
        objectUnderTest.registerHealthCheck(new SimpleHealthCheck(false));

        //Assert
        assertFalse(objectUnderTest.isHealthy());
        assertEquals(2, objectUnderTest.diagnostics().size());
        assertTrue(objectUnderTest.diagnostics().get(0).isHealthy());
        assertFalse(objectUnderTest.diagnostics().get(1).isHealthy());
    }

    @Test
    void testJexxaVersion()
    {
        //Arrange --

        //Act
        var jexxaVersion = objectUnderTest.jexxaVersion();

        //Assert
        assertFalse(jexxaVersion.version().isEmpty());
        assertFalse(jexxaVersion.buildTimestamp().isEmpty());
        assertFalse(jexxaVersion.projectName().isEmpty());
        assertFalse(jexxaVersion.repository().isEmpty());
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
