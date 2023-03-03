package io.jexxa.infrastructure.healthcheck;

import java.time.Duration;

public final class HealthIndicators
{
    public static TimoutIndicator timerMonitor(Duration maxTimeout )
    {
        return new TimoutIndicator(maxTimeout);
    }

    private HealthIndicators()
    {
        //Private constructors
    }
}
