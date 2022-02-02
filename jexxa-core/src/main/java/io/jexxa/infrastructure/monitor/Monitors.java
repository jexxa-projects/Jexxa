package io.jexxa.infrastructure.monitor;

import java.time.Duration;

public class Monitors
{
    public static TimeoutMonitor timeoutMonitor( Duration maxTimeout )
    {
        return new TimeoutMonitor(maxTimeout);
    }

    private Monitors()
    {
        //Private constructors
    }
}
