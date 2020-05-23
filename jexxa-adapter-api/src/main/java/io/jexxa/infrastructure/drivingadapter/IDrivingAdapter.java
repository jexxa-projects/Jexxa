package io.jexxa.infrastructure.drivingadapter;

/**
 * Generic interface that must be implemented by a DrivingAdapter
 *
 * A driving adapter has internally provide following states:
 * CREATED: In this state is a newly created driving adapter. Following methods are allowed:
 * - {@link #register(Object)} which performs no state change
 * - {@link #start()} which performs a state change into state STARTED
 * STARTED:
 * - Within this state incoming calls can be forwarded to ports
 * - Important note: Before forwarding an incoming call a Driving Adapter has to ensure that this call is synchronized using {@link #acquireLock()}
 * - {@link #stop()} to change into state STOPPED
 * STOPPED:
 * - In this state a driving adapter can no longer be used
 */
public interface IDrivingAdapter
{
    /**
     * Register an object that should accessed by this driving adapter  
     * @param port: port to be registered with driving adapter.
     */
    void register(Object port);

    /**
     * Perform all operations that are required to offer registered objects via this driving adapter.
     */
    void start();

    /**
     * Perform all operations that are required to deallocate resources and no longer offer registered objects.
     * As soon as an object is stopped it is not required that it can be started again. 
     */
    void stop();

    /**
     * Returns a SynchronizationFacade that must be used to ensure synchronized access to ports
     *
     * @return SynchronizationFacade which must be used to ensure synchronized access to ports   
     */
    static SynchronizationFacade acquireLock()
    {
        return new SynchronizationFacade();
    }
}
