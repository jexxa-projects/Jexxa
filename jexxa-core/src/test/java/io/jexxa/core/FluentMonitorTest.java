package io.jexxa.core;

import io.jexxa.application.infrastructure.drivingadapter.ProxyAdapter;
import io.jexxa.application.infrastructure.drivingadapter.ProxyPortAdapter;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.jexxa.infrastructure.monitor.Monitors.timerMonitor;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FluentMonitorTest {

    @Test
    void incomingCalls()
    {
        //Arrange
        var jexxaMain = new JexxaMain(FluentMonitorTest.class);
        var maxTimeout = Duration.ofSeconds(2);
        var boundedContext = jexxaMain.getBoundedContext();

        jexxaMain.addToInfrastructure("io.jexxa.application.infrastructure.drivingadapter")
                .bind(ProxyAdapter.class).to(ProxyPortAdapter.class);

        //Act
        jexxaMain.monitor(ProxyPortAdapter.class).incomingCalls(timerMonitor(maxTimeout));

        var firstResult = boundedContext.isHealthy();

        await()
                .atMost(3, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> !boundedContext.isHealthy());

        // / Assert
        assertTrue(firstResult); // After start status should be healthy
        assertFalse(boundedContext.isHealthy());
    }

}