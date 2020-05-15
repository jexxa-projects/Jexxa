package io.jexxa.infrastructure.drivingadapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SynchronizationFacade
{
    private static final Object GLOBAL_SYNCHRONIZATION_OBJECT = new Object();

    public Object invoke(Method method, Object object, Object[] args ) throws InvocationTargetException, IllegalAccessException
    {
        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            return method.invoke(object, args);
        }
    }

    SynchronizationFacade() //Package protected constructor 
    {
    }
}
