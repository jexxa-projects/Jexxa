package io.jexxa.adapterapi.invocation;

import java.util.HashMap;

public final class InvocationManager {
    private static final HashMap<Object, DefaultInvocationHandler> INVOCATION_HANDLER_MAP = new HashMap<>();


    public static InvocationHandler getInvocationHandler(Object object)
    {
        return INVOCATION_HANDLER_MAP.computeIfAbsent(object, key -> createDefaultInvocationHandler());
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
