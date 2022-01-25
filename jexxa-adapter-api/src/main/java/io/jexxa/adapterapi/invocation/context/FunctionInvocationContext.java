package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Function;

public class FunctionInvocationContext<T, R> extends InvocationContext
{
    private final Function<T, R> function;
    private final T argument;
    private R returnValue;

    public FunctionInvocationContext(Function<T, R> function, T argument, Collection<AroundInterceptor> interceptors)
    {
        super(interceptors);
        this.function = function;
        this.argument = argument;
    }

    @Override
    public void invoke() throws InvocationTargetException, IllegalAccessException
    {
        returnValue = function.apply(argument);
    }

    @Override
    public Method getMethod() {
        try {
            return function.getClass().getMethod("apply");
        } catch (NoSuchMethodException | SecurityException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object getTarget() {
        return function;
    }

    @Override
    public Object[] getArgs() {
        return new Object[]{argument};
    }

    @Override
    public R getReturnValue() {
        return returnValue;
    }
}
