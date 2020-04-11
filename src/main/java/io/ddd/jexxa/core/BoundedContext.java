package io.ddd.jexxa.core;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.ddd.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;

public class BoundedContext
{
    private static final Logger logger = JexxaLogger.getLogger(BoundedContext.class);

    private final Lock lock = new ReentrantLock();
    private final Condition runningCondition  = lock.newCondition();
    private boolean isRunning = false;

    private final String contextName;
    private final Clock clock = Clock.systemUTC();
    private final Instant startTime;

    BoundedContext(final String contextName)
    {
        this.startTime = clock.instant();
        this.contextName = contextName;
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

    @SuppressWarnings({"java:S2189", "java:S2589"})
    public void run()
    {
        lock.lock();
        if (isRunning()) {
            lock.unlock();
            return;
        }

        isRunning = true;
        
        try {
            while (isRunning()) {
                Duration uptime = uptime();
                logger.info("Bounded Context {} started in {}.{} sec", contextName, uptime.toSeconds(), uptime.toMillisPart());
                runningCondition.await();
            }
        }
        catch (Exception e)
        {
            lock.unlock();
        }
        finally
        {
            lock.unlock();
        }
    }

    public void shutdown()
    {
        lock.lock();
        if ( ! isRunning() )
        {
            lock.unlock();
            return;
        }

        isRunning = false;
        try {
            runningCondition.signal();
        }
        finally
        {
            lock.unlock();
        }
    }

    public synchronized boolean isRunning()
    {
        return isRunning;
    }
    
}
