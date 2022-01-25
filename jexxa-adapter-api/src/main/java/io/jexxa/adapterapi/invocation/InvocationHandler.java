package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableRunnable;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    void invoke(Object targetObject, SerializableRunnable runnable) ;

    <T> void invoke(Object targetObject,SerializableConsumer<T> consumer, T argument);

    <T> T invoke(Object targetObject,SerializableSupplier<T> supplier);

    <T, R> R invoke(Object targetObject,SerializableFunction<T, R> function, T argument);

}
