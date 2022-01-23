package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;

import java.lang.reflect.InvocationTargetException;

public interface Interceptor {

    default void before( InvocationContext invocationContext )  {}

    default void after( InvocationContext invocationContext ) {}

    default void surround( InvocationContext invocationContext ) throws InvocationTargetException, IllegalAccessException {}

}
