package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.function.SerializableConsumer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class ConsumerInvocationContext<T> extends InvocationContext
{
    private final SerializableConsumer<T> consumer;
    private final T argument;
    Method method;


    public ConsumerInvocationContext(Object targetObject,
                                     SerializableConsumer<T> consumer,
                                     T argument,
                                     Collection<AroundInterceptor> interceptors)
    {
        super(targetObject,interceptors);
        this.consumer = consumer;
        this.argument  = argument;
    }

    @Override
    public void invoke()
    {
        consumer.accept(argument);
    }

    @Override
    public Method getMethod() {
        if (method == null)
        {
            method = LambdaUtils.getImplMethod(getTarget(), consumer);
        }
        return method;
    }

    @Override
    public Object getTarget() {
        return consumer;
    }

    @Override
    public Object[] getArgs() {
        return new Object[]{argument};
    }

    @Override
    public Object getReturnValue() {
        return null;
    }
}
