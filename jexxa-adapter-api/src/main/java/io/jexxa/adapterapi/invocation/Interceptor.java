package io.jexxa.adapterapi.invocation;

import java.lang.reflect.InvocationTargetException;

public interface Interceptor {

    default void before( InvocationContext invocationContext ) throws InvocationTargetException, IllegalAccessException {}

    default void after( InvocationContext invocationContext ) {}

    default void surround( InvocationContext invocationContext ) throws InvocationTargetException, IllegalAccessException {}

}
