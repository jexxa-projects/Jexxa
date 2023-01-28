package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class InvocationContext
{
    private final Iterator<AroundInterceptor> currentInterceptor;
    private final Object targetObject;

    protected InvocationContext(Object targetObject, Collection<AroundInterceptor> interceptors)
    {
        this.currentInterceptor = Objects.requireNonNull(interceptors).iterator();
        this.targetObject = targetObject;
    }

    /**
     * This method performs a method invocation on given method.
     *
     * @throws InvocationTargetRuntimeException forwards exception from Java's reflective API because it cannot be handled here in a meaningful way
     */
    public abstract void invoke();

    public abstract Method getMethod();

    public Object getTarget()
    {
        return targetObject;
    }

    public <T> T getTarget(Class<T> clazz)
    {
        return clazz.cast(getTarget());
    }


    public abstract Object[] getArgs();

    public abstract Object getReturnValue();

    public <T> T getReturnValue(Class<T> clazz)
    {
        return clazz.cast(getReturnValue());
    }

    public Class<?>[] getArgTypes()
    {
        return Stream.of(getArgs()).map(Object::getClass).toArray(Class<?>[]::new);
    }

    public void proceed()
    {
        if (currentInterceptor.hasNext())
        {
            currentInterceptor.next().around(this);
        } else {
            invoke();
        }

    }
}
