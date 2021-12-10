package io.jexxa.core;

import io.jexxa.utils.JexxaCoreProperties;
import io.jexxa.utils.JexxaLogger;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

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

    public VersionInfo getJexxaVersion()
    {
        return JexxaVersion.getJexxaVersion();
    }

    public VersionInfo getContextVersion()
    {
        var properties = jexxaMain.getProperties();

        return VersionInfo.of()
                .version(properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_VERSION, ""))
                .repository(properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_REPOSITORY, ""))
                .buildTimestamp(properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_BUILD_TIMESTAMP, ""))
                .projectName(properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_NAME, ""))
                .create();
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
