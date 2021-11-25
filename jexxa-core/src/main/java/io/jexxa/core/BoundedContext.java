package io.jexxa.core;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public class BoundedContext
{
    public static final String CONTEXT_VERSION = "io.jexxa.context.version";
    public static final String CONTEXT_REPOSITORY = "io.jexxa.context.repository";
    public static final String CONTEXT_NAME = "io.jexxa.context.name";
    public static final String CONTEXT_TIMESTAMP = "io.jexxa.context.build.timestamp";

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

    /**
     * @deprecated Will be removed in future releases, so that this class can be exposed to get Information about
     * the context
     */
    @Deprecated(forRemoval = true)
    public synchronized void shutdown()
    {
        internalShutdown();
    }


    public VersionInfo getJexxaVersion()
    {
        return JexxaVersion.getJexxaVersion();
    }

    public VersionInfo getContextVersion()
    {
        var properties = jexxaMain.getProperties();

        return VersionInfo.of()
                .version(properties.getProperty(CONTEXT_VERSION, ""))
                .repository(properties.getProperty(CONTEXT_REPOSITORY, ""))
                .buildTimestamp(properties.getProperty(CONTEXT_TIMESTAMP, ""))
                .projectName(properties.getProperty(CONTEXT_NAME, ""))
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
