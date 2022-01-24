package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;

import java.lang.reflect.InvocationTargetException;

public class DefaultInterceptor implements Interceptor
{

    @Override
    public void before(InvocationContext invocationContext) { /* default implementation */ }

    @Override
    public void after(InvocationContext invocationContext) { /* default implementation */ }

    @Override
    public void around(InvocationContext invocationContext) throws InvocationTargetException, IllegalAccessException
    {
        invocationContext.proceed();
    }
}
