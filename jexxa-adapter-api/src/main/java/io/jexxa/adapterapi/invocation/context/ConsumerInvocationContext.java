package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Consumer;

public class ConsumerInvocationContext<T> extends InvocationContext
{
    private final Consumer<T> consumer;
    private final T argument;


    public ConsumerInvocationContext(Object targetObject, Consumer<T> consumer, T argument, Collection<AroundInterceptor> interceptors) {
        super(targetObject,interceptors);
        this.consumer = consumer;
        this.argument  = argument;
    }

    @Override
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        consumer.accept(argument);
    }

    @Override
    public Method getMethod() {
        try {
            return consumer.getClass().getMethod("accept", Object.class);
        } catch (NoSuchMethodException | SecurityException e)
        {
            throw new IllegalStateException(e);
        }
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
