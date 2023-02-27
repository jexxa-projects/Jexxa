package io.jexxa.adapterapi.invocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InvocationManager {
    private static final Map<Object, DefaultInvocationHandler> INVOCATION_HANDLER_MAP = new ConcurrentHashMap<>();


    public static synchronized JexxaInvocationHandler getInvocationHandler(Object object)
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
