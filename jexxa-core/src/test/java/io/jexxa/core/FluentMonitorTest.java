package io.jexxa.core;

import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.monitor.AroundMonitor;
import io.jexxa.application.JexxaTestApplication;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.infrastructure.drivingadapter.generic.ProxyDrivingAdapter;
import io.jexxa.application.infrastructure.drivingadapter.portadapter.ProxyPortAdapter;
import io.jexxa.application.infrastructure.drivingadapter.generic.ProxyAdapter;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.jexxa.infrastructure.monitor.Monitors.timerMonitor;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FluentMonitorTest {

    @Test
    void beforeMonitor()
    {
        //Arrange
        var jexxaMain = new JexxaMain(JexxaTestApplication.class);
        var maxTimeout = Duration.ofSeconds(2);
        var boundedContext = jexxaMain.getBoundedContext();

        jexxaMain.bind(ProxyDrivingAdapter.class).to(ProxyPortAdapter.class);

        //Act
        jexxaMain.monitor(ProxyPortAdapter.class).with(timerMonitor(maxTimeout));

        var firstResult = boundedContext.isHealthy();

        await()
                .atMost(3, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> !boundedContext.isHealthy());

        // / Assert
        assertTrue(firstResult); // After start status should be healthy
        assertFalse(boundedContext.isHealthy());
    }

    @Test
    void aroundMonitor()
    {
        //Arrange
        var jexxaMain = new JexxaMain(JexxaTestApplication.class);
        var boundedContext = jexxaMain.getBoundedContext();
        var objectUnderTest = jexxaMain.getInstanceOfPort(SimpleApplicationService.class);

        var proxyAdapter = new ProxyAdapter();
        proxyAdapter.register(objectUnderTest);
        jexxaMain.monitor(SimpleApplicationService.class).with(new ExceptionMonitor());

        // Act
        assertThrows(NullPointerException.class, () -> proxyAdapter.invoke(objectUnderTest::throwNullPointerException));

        // Assert
        assertFalse(boundedContext.isHealthy());
    }

    private static class ExceptionMonitor extends AroundMonitor {

        private RuntimeException occurredException;

        @Override
        public boolean healthy() {
            return occurredException == null;
        }

        @Override
        public String getStatusMessage() {
            if (occurredException == null)
            {
                return "All fine!";
            }
            return occurredException.getMessage();
        }

        @Override
        public void around(InvocationContext invocationContext) {
            try {
                invocationContext.invoke();
            } catch (RuntimeException e)
            {
                occurredException = e;
                throw e;
            }
        }
    }
}