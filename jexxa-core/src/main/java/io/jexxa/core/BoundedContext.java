package io.jexxa.core;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import io.jexxa.utils.JexxaLogger;

public class BoundedContext
{
    private boolean isRunning = false;
    private boolean isWaiting = false;

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

    @SuppressWarnings("unused")
    public Duration uptime()
    {
        return Duration.between(startTime, clock.instant());
    }

    @SuppressWarnings("unused")
    public String contextName()
    {
        return contextName;
    }


    public synchronized void shutdown()
    {
        if ( isWaiting )
        {
            isWaiting = false;
            notifyAll();
        }
    }

    
    public boolean isRunning()
    {
        return isRunning;
    }

    public synchronized JexxaMain waitForShutdown()
    {
        setupSignalHandler();
        isWaiting = true;

        try
        {
            while ( isWaiting ) {
                this.wait();
            }
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }

        return jexxaMain;
    }

    protected synchronized void start()
    {
        isRunning = true;
    }

    protected void stop()
    {
        isRunning = false;
        shutdown();
    }

    private void setupSignalHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            JexxaLogger.getLogger(JexxaMain.class).info("Shutdown signal received ...");
            jexxaMain.stop();
        }));
    }


}
