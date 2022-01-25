package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.function.SerializableRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import static io.jexxa.adapterapi.invocation.context.LambdaUtils.getSerializedLambda;

public class RunnableInvocationContext extends InvocationContext
{
    private final SerializableRunnable runnable;
    private final Object targetObject;

    public RunnableInvocationContext(Object targetObject, SerializableRunnable runnable, Collection<AroundInterceptor> interceptors)
    {
        super(targetObject,interceptors);
        this.runnable = runnable;
        this.targetObject = targetObject;
    }

    @Override
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        runnable.run();
    }

    @Override
    public Method getMethod() {
        try {
            return getTarget().getClass().getMethod(Objects.requireNonNull(getSerializedLambda(runnable)).getImplMethodName());
        } catch (NoSuchMethodException | SecurityException e)
        {
            throw new IllegalStateException(e);
        }
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
