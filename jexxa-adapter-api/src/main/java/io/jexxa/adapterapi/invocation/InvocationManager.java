package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.interceptor.RootInterceptor;

import java.util.HashMap;

public class InvocationManager {
    private static final HashMap<Object, RootInterceptor> invocationHandlerMap = new HashMap<>();


    public static InvocationHandler getInvocationHandler(Object object)
    {
        if ( ! invocationHandlerMap.containsKey(object) )
        {
            invocationHandlerMap.put(object, createDefaultInvocationHandler());
        }

        return invocationHandlerMap.get(object);
    }

    public static RootInterceptor getRootInterceptor(Object object)
    {
        return (RootInterceptor) getInvocationHandler(object);
    }

    private static RootInterceptor createDefaultInvocationHandler()
    {
        return new RootInterceptor();
    }

    private InvocationManager()
    {
        //private constructor
    }
}
