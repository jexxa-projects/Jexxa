package io.jexxa.adapterapi.invocation;

import java.util.HashMap;

public class InvocationManager {
    private static final HashMap<Object, DefaultInvocationHandler> invocationHandlerMap = new HashMap<>();


    public static InvocationHandler getInvocationHandler(Object object)
    {
        if ( ! invocationHandlerMap.containsKey(object) )
        {
            invocationHandlerMap.put(object, createDefaultInvocationHandler());
        }

        return invocationHandlerMap.get(object);
    }

    public static DefaultInvocationHandler getRootInterceptor(Object object)
    {
        return (DefaultInvocationHandler) getInvocationHandler(object);
    }

    private static DefaultInvocationHandler createDefaultInvocationHandler()
    {
        return new DefaultInvocationHandler();
    }

    private InvocationManager()
    {
        //private constructor
    }
}
