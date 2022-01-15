package io.jexxa.adapterapi.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Consumer;

public class SynchronizationFacade implements Interceptor
{
    private static final Object GLOBAL_SYNCHRONIZATION_OBJECT = new Object();
    private static SynchronizationFacade defaultSynchronizationFacade = new SynchronizationFacade();

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
        var invocationContext = new InvocationContext(method, object, args);
        surround(invocationContext);
        return invocationContext.getReturnValue();
    }

    @Override
    public void surround( InvocationContext invocationContext ) throws InvocationTargetException, IllegalAccessException {
        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            invocationContext.invoke();
        }
    }


    /**
     * This method performs a synchronized method invocation on given method. Note: If this method is not used by a driving adapter it must
     * use return value {@link #getSynchronizationObject()} to synchronize method invocation on a port.
     *
     * @param consumer Consumer that should be called. Must not be null
     * @param argument argument that should be called
     */
    public <T> void invoke(Consumer<T> consumer, T argument)
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
    /*public <T, R> R invoke(Function<T, R> function, T argument )
    {
        Objects.requireNonNull(function);
        Objects.requireNonNull(argument);

        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            return function.apply(argument);
        }
    }*/

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

    /**
     * Returns a SynchronizationFacade that must be used to ensure synchronized access to ports
     *
     * @return SynchronizationFacade which must be used to ensure synchronized access to ports
     */
    public static SynchronizationFacade acquireLock(Class<?> invokedClazz)
    {
        return defaultSynchronizationFacade;
    }

    protected static void setSynchronizationFacade(SynchronizationFacade synchronizationFacade)
    {
        defaultSynchronizationFacade = synchronizationFacade;
    }

    SynchronizationFacade()
    {
        //Package protected constructor
    }
}
