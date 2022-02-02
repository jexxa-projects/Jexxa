package io.jexxa.infrastructure.monitor;


import io.jexxa.adapterapi.drivingadapter.HealthCheck;
import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;

import java.time.Duration;
import java.time.Instant;

public class TimerMonitor extends HealthCheck implements BeforeInterceptor
{
    private Instant lastUpdate = Instant.now();
    private final Duration messageTimeout;

    public TimerMonitor(Duration messageTimeout)
    {
        this.messageTimeout = messageTimeout;
    }

    public void before(InvocationContext invocationContext)
    {
        lastUpdate = Instant.now();
    }

    @Override
    public boolean healthy()
    {
        return Duration.between(lastUpdate, Instant.now()).toMillis() <= messageTimeout.toMillis();
    }

    @Override
    public String getStatusMessage()
    {
        if (!healthy())
        {
            return "Did not receive a message for object "
                    + getObservedObject().getClass().getSimpleName()
                    + " within period of "
                    + Duration.between(lastUpdate, Instant.now()).toSeconds()
                    + " seconds.";
        }

        return "All fine...";
    }
}