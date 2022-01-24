package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;

import java.lang.reflect.InvocationTargetException;

public interface Interceptor {

    void before( InvocationContext invocationContext );

    void after( InvocationContext invocationContext );

    void around(InvocationContext invocationContext ) throws InvocationTargetException, IllegalAccessException;
}
