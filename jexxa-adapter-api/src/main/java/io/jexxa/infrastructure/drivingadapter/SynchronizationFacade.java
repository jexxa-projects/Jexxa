package io.jexxa.infrastructure.drivingadapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class SynchronizationFacade
{
    private static final Object GLOBAL_SYNCHRONIZATION_OBJECT = new Object();

    /**
     * This method performs a synchronized method invocation on given method. Note: If this method is not used by a driving adapter it must
     * use return value {@link #getSynchronizationObject()} to synchronize method invocation on a port.
     *
     * @param method Method that should be called. Must not be null
     * @param object concrete instance of the object on which the method should be called
     * @param args attributes
     * @return result of the method
     * @throws InvocationTargetException forwards exception from Java's reflective API because it cannot be handled here in a meaningful way
     * @throws IllegalAccessException forwards exception from Java's reflective API because it cannot be handled here in a meaningful way
     */
    public Object invoke(Method method, Object object, Object[] args ) throws InvocationTargetException, IllegalAccessException
    {
        Objects.requireNonNull(method, "Method in SynchronizationFacade must not be null ");
        Objects.requireNonNull(object, "Object in SynchronizationFacade must not be null ");
        Objects.requireNonNull(args, "Args in SynchronizationFacade must not be null ");

        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            return method.invoke(object, args);
        }
    }

    /**
     * If a driving adapter does not use {@link #invoke(Method, Object, Object[])} to call an
     *
     * @return Object that must be used in synchronized-block
     */
    public Object getSynchronizationObject()
    {
        return GLOBAL_SYNCHRONIZATION_OBJECT;
    }

    SynchronizationFacade()
    {
        //Package protected constructor
    }
}
