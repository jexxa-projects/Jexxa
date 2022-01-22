package io.jexxa.adapterapi.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface InvocationHandler
{
    void invoke(InvocationContext invocationContext) throws InvocationTargetException, IllegalAccessException;
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
}
