package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.interceptor.Interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Objects;

public class InvocationContext {
    private final Method method;
    private final Object object;
    private final Object[] args;

    private Object returnValue;
    private final Iterator<Interceptor> startingIterator;

    public InvocationContext(Method method, Object object, Object[] args, Iterator<Interceptor> startingIterator)
    {
        this.method = Objects.requireNonNull( method );
        this.object = Objects.requireNonNull( object );
        this.args = Objects.requireNonNull( args );
        this.startingIterator = startingIterator;
    }

    /**
     * This method performs a method invocation on given method.
     *
     * @throws InvocationTargetException forwards exception from Java's reflective API because it cannot be handled here in a meaningful way
     */
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        returnValue = method.invoke(object, args);
    }

    public Method getMethod() {
        return method;
    }

    public Object getTarget() {
        return object;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public <T> T getReturnValue(Class<T> clazz)
    {
        return clazz.cast(returnValue);
    }

    public void proceed() throws InvocationTargetException, IllegalAccessException
    {
        if (startingIterator.hasNext())
        {
            startingIterator.next().around(this);
        } else {
            invoke();
        }
    }
}
