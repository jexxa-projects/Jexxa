package io.jexxa.infrastructure.drivingadapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

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
        Objects.requireNonNull(method);
        Objects.requireNonNull(object);
        Objects.requireNonNull(args);

        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            return method.invoke(object, args);
        }
    }

    /**
     * This method performs a synchronized method invocation on given method. Note: If this method is not used by a driving adapter it must
     * use return value {@link #getSynchronizationObject()} to synchronize method invocation on a port.
     *
     * @param consumer Consumer that should be called. Must not be null
     * @param argument argument that should be called
     */
    public <T> void invoke(Consumer<T> consumer, T argument )
    {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(argument);

        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            consumer.accept(argument);
        }
    }

    /**
     * This method performs a synchronized method invocation on given method. Note: If this method is not used by a driving adapter it must
     * use return value {@link #getSynchronizationObject()} to synchronize method invocation on a port.
     *
     * @param function Consumer that should be called. Must not be null
     * @param argument argument that should be called
     */
    public <T, R> R invoke(Function<T, R> function, T argument )
    {
        Objects.requireNonNull(function);
        Objects.requireNonNull(argument);

        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            return function.apply(argument);
        }
    }

    /**
     * If a driving adapter does not use {@link #invoke(Method, Object, Object[])} to call an
     *
     * @deprecated Will be replaced by invoke methods
     * @return Object that must be used in synchronized-block
     */
    @Deprecated(forRemoval = true)
    public Object getSynchronizationObject()
    {
        return GLOBAL_SYNCHRONIZATION_OBJECT;
    }

    SynchronizationFacade()
    {
        //Package protected constructor
    }
}
