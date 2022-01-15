package io.jexxa.adapterapi.invocation;

import java.lang.reflect.InvocationTargetException;

public interface InvocationHandler
{
    void invoke(InvocationContext invocationContext) throws InvocationTargetException, IllegalAccessException;
}
