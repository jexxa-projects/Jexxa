package io.jexxa.adapterapi.invocation;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface InvocationHandler
{
    /**
     * This method performs a synchronized method invocation on given method.
     *
     * @param method Method that should be called. Must not be null
     * @param object concrete instance of the object on which the method should be called
     * @param args attributes
     * @return result of the method
     * @throws InvocationTargetException forwards exception from Java's reflective API because it cannot be handled here in a meaningful way
     * @throws IllegalAccessException forwards exception from Java's reflective API because it cannot be handled here in a meaningful way
     */
    Object invoke(Method method, Object object, Object[] args ) throws InvocationTargetException, IllegalAccessException;

    void invoke(Object targetObject, JRunnable runnable) ;

    <T> void invoke(Consumer<T> consumer, T argument);

    <T> T invoke(Supplier<T> supplier);

    <T, R> R invoke(Function<T, R> function, T argument);

    @FunctionalInterface
    public static interface JRunnable extends Runnable, Serializable
    {

    }
}
