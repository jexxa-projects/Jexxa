package io.jexxa.core;

import io.jexxa.adapterapi.drivingadapter.Diagnostics;
import io.jexxa.adapterapi.drivingadapter.HealthCheck;
import io.jexxa.common.JexxaCoreProperties;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.jexxa.common.facade.logger.SLF4jLogger.getLogger;


public final class BoundedContext
{
    private boolean isRunning = false;
    private boolean isWaiting = false;
    private final String contextName;
    private final Clock clock = Clock.systemUTC();
    private final Instant startTime;
    private final JexxaMain jexxaMain;
    private final List<HealthCheck> healthChecks = new ArrayList<>();

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

    public VersionInfo jexxaVersion()
    {
        return JexxaVersion.getJexxaVersion();
    }

    public VersionInfo contextVersion()
    {
        var properties = jexxaMain.getProperties();

        return VersionInfo.of()
                .version(properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_VERSION, ""))
                .repository(properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_REPOSITORY, ""))
                .buildTimestamp(properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_BUILD_TIMESTAMP, ""))
                .projectName(properties.getProperty(JexxaCoreProperties.JEXXA_CONTEXT_NAME, ""))
                .create();
    }


    public synchronized boolean isRunning()
    {
        return isRunning;
    }

    /**
     * Returns true if all HealthChecks returns true. If at least one HealthCheck return false, this method returns false as well
     *
     * @return True if all HealthChecks return true, otherwise false.
     */
    public boolean isHealthy()
    {
        var isHealthy = healthChecks.stream()
                .map(HealthCheck::healthy)
                .filter( element -> !element)
                .findFirst();

        return isHealthy.orElse(true);
    }

    public List<Diagnostics> diagnostics()
    {
        return healthChecks
                .stream()
                .map(HealthCheck::getDiagnostics)
                .toList();
    }

    void registerHealthCheck( HealthCheck healthCheck)
    {
        healthChecks.add(healthCheck);
    }

    synchronized JexxaMain waitForShutdown()
    {
        if (!isRunning)
        {
            return jexxaMain;
        }

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

    synchronized void start()
    {
        isRunning = true;
    }

    synchronized void stop()
    {
        isRunning = false;
        internalShutdown();
    }

    private void setupSignalHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            getLogger(JexxaMain.class).info("Shutdown signal received ...");
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
