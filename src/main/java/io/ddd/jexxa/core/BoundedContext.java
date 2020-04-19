package io.ddd.jexxa.core;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class BoundedContext
{
    private boolean isRunning = false;

    private final String contextName;
    private final Clock clock = Clock.systemUTC();
    private final Instant startTime;
    private final JexxaMain jexxaMain;

    BoundedContext(final String contextName, JexxaMain jexxaMain)
    {
        this.startTime = clock.instant();
        this.contextName = contextName;
        this.jexxaMain = jexxaMain;
    }

    public Duration uptime()
    {
        return Duration.between(startTime, clock.instant());
    }

    @SuppressWarnings("unused")
    public String contextName()
    {
        return contextName;
    }

    void start()
    {
        isRunning = true;
    }

    void stop()
    {
        isRunning = false;
    }

    public void shutdown()
    {
        isRunning = false;
        jexxaMain.notifyShutdown();
    }

    
    public boolean isRunning()
    {
        return isRunning;
    }
    
}
