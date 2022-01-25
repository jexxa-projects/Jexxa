package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;

import java.lang.reflect.InvocationTargetException;

public interface AroundInterceptor  {

    void around(InvocationContext invocationContext ) throws InvocationTargetException, IllegalAccessException;
}
