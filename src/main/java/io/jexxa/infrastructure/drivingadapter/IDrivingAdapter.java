package io.jexxa.infrastructure.drivingadapter;

/**
 * Generic interface to start/stop a DrivingAdapter
 */
public interface IDrivingAdapter
{
    void start();

    void stop();

    /**
     * @param port: port to be registered with driving adapter.
     */
    void register(Object port);

    static SynchronizationFacade acquireLock()
    {
        return new SynchronizationFacade();
    }
}
