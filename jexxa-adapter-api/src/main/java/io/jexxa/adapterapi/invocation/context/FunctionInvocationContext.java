package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

public class FunctionInvocationContext<T, R> extends InvocationContext
{
    private final SerializableFunction<T, R> function;
    private final T argument;
    private R returnValue;
    Method method;

    public FunctionInvocationContext(Object targetObject,
                                     SerializableFunction<T, R> function,
                                     T argument,
                                     Collection<AroundInterceptor> interceptors)
    {
        super(targetObject,interceptors);
        this.function = function;
        this.argument = argument;
    }

    @Override
    public void invoke()
    {
        returnValue = function.apply(argument);
    }

    @Override
    public Method getMethod() {
        if (method == null)
        {
            method = LambdaUtils.getImplMethod(getTarget(), function);
        }
        return method;
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
