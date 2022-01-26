package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.function.SerializableRunnable;

import java.lang.reflect.Method;
import java.util.Collection;

public class RunnableInvocationContext extends InvocationContext
{
    private final SerializableRunnable runnable;
    private final Object targetObject;
    private Method method;

    public RunnableInvocationContext(Object targetObject, SerializableRunnable runnable, Collection<AroundInterceptor> interceptors)
    {
        super(targetObject,interceptors);
        this.runnable = runnable;
        this.targetObject = targetObject;
    }

    @Override
    public void invoke()
    {
        runnable.run();
    }

    @Override
    public Method getMethod()
    {
        if (method == null)
        {
           method = LambdaUtils.getImplMethod(getTarget(), runnable, getArgTypes());
        }
        return method;
    }

    @Override
    public Object getTarget() {
        return targetObject;
    }

    @Override
    public Object[] getArgs() {
        return new Object[0];
    }

    @Override
    public Object getReturnValue() {
        return null;
    }
}
