package io.jexxa.core;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
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
        this.contextName = Objects.requireNonNull(contextName);
        this.jexxaMain = Objects.requireNonNull(jexxaMain);
    }

    public Duration uptime()
    {
        return Duration.between(startTime, clock.instant());
    }

    public String contextName()
    {
        return contextName;
    }

    @Deprecated(forRemoval = true)
    public synchronized void shutdown()
    {
        internalShutdown();
    }



    public boolean isRunning()
    {
        return isRunning;
    }

    synchronized JexxaMain waitForShutdown()
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
        internalShutdown();
    }

    private void setupSignalHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            JexxaLogger.getLogger(JexxaMain.class).info("Shutdown signal received ...");
            jexxaMain.stop();
        }));
    }

    private synchronized void internalShutdown()
    {
        if ( isWaiting )
        {
            isWaiting = false;
            notifyAll();
        }
    }

}
