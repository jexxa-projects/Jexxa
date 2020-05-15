package io.jexxa.infrastructure.drivingadapter;

/**
 * Generic interface to start/stop a DrivingAdapter
 *  */
public interface IDrivingAdapter
{
    void start();

    void stop();

    /*
     * @param object: Object to be registered.
     * @pre object: Must not be null
     */
    void register(Object object);

    static SynchronizationFacade acquireLock()
    {
        return new SynchronizationFacade();
    }
}
