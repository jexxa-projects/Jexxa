package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableRunnable;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;

import java.lang.reflect.Method;

public interface JexxaInvocationHandler
{
    /**
     * This method performs a method invocation on given method.
     *
     * @param method Method that should be called. Must not be null
     * @param object concrete instance of the object on which the method should be called
     * @param args attributes
     * @return result of the method
     * @throws InvocationTargetRuntimeException includes occurred exceptions.
     */
    Object invoke(Method method, Object object, Object[] args );

    /**
     * This method performs a method invocation on given runnable.
     *
     * @param targetObject to invoke method on
     * @param runnable functional interface to be executed
     * @throws InvocationTargetRuntimeException includes occurred exceptions.
     */
    void invoke(Object targetObject, SerializableRunnable runnable) ;

    /**
     * This method performs a method invocation on given consumer.
     *
     * @param targetObject to invoke method on
     * @param consumer functional interface to be executed
     * @param argument for the functional interface
     * @throws InvocationTargetRuntimeException includes occurred exceptions.
     */
    <T> void invoke(Object targetObject,SerializableConsumer<T> consumer, T argument);

    /**
     * This method performs a method invocation on given supplier.
     *
     * @param targetObject to invoke method on
     * @param supplier functional interface to be executed
     * @return T return value of the functional interface
     * @throws InvocationTargetRuntimeException includes occurred exceptions.
     */
    <T> T invoke(Object targetObject,SerializableSupplier<T> supplier);

    /**
     * This method performs a method invocation on given function.
     *
     * @param targetObject to invoke method on
     * @param function functional interface to be executed
     * @param argument for the functional interface
     * @return T return value of the functional interface
     * @throws InvocationTargetRuntimeException includes occurred exceptions.
     */
    <T, R> R invoke(Object targetObject,SerializableFunction<T, R> function, T argument);
}
