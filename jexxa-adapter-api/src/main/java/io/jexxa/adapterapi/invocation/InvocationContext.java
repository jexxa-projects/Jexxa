package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

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
     * @throws InvocationTargetException forwards exception from Java's reflective API because it cannot be handled here in a meaningful way
     */
    public abstract void invoke() throws InvocationTargetException, IllegalAccessException ;

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

    public void proceed()  {
        try {
            if (currentInterceptor.hasNext())
            {
                currentInterceptor.next().around(this);
            } else {
                    invoke();
            }
        } catch ( InvocationTargetException e)
        {
            throw new InvocationTargetRuntimeException( e.getTargetException() );
        } catch ( IllegalAccessException e )
        {
            throw new InvocationTargetRuntimeException( e );
        }
    }
}
