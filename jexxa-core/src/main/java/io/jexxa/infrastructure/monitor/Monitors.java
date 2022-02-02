package io.jexxa.infrastructure.monitor;

import java.time.Duration;

public class Monitors
{
    public static TimerMonitor timerMonitor(Duration maxTimeout )
    {
        return new TimerMonitor(maxTimeout);
    }

    private Monitors()
    {
        //Private constructors
    }
}