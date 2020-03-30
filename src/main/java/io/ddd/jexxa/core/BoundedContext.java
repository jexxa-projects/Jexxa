package io.ddd.jexxa.core;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.ddd.jexxa.utils.JexxaLogger;

public class BoundedContext
{
    private final Lock lock = new ReentrantLock();
    private final Condition runningCondition  = lock.newCondition();
    private boolean isRunning = false;

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
                JexxaLogger.getLogger(BoundedContext.class).info("Starting Bounded Context " );
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
