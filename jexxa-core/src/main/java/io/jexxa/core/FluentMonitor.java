package io.jexxa.core;

import io.jexxa.adapterapi.drivingadapter.HealthCheck;
import io.jexxa.adapterapi.interceptor.AfterInterceptor;
import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.adapterapi.invocation.monitor.AfterMonitor;
import io.jexxa.adapterapi.invocation.monitor.AroundMonitor;
import io.jexxa.adapterapi.invocation.monitor.BeforeMonitor;

import java.util.Objects;

@SuppressWarnings({"unused"})
public final class FluentMonitor
{
    private final Object targetObject;
    private final JexxaMain jexxaMain;

    FluentMonitor(JexxaMain jexxaMain, Object targetObject )
    {
        this.targetObject = Objects.requireNonNull( targetObject );
        this.jexxaMain = Objects.requireNonNull( jexxaMain );
    }

    /**
     * @deprecated Use typed method {@link #with(BeforeMonitor)}
     */
    @SuppressWarnings("UnusedReturnValue")
    @Deprecated(forRemoval = true)
    public <U extends HealthCheck & BeforeInterceptor> JexxaMain incomingCalls(U monitor)
    {
        monitor.setObservedObject(targetObject);
        InvocationManager.getRootInterceptor(targetObject).registerBefore(monitor);
        return jexxaMain.registerHealthCheck(monitor);
    }

    /**
     * @deprecated Use typed method {@link #with(AfterMonitor)}
     */
    @Deprecated(forRemoval = true)
    public <U extends HealthCheck & AfterInterceptor> JexxaMain outgoingCalls(U monitor)
    {
        InvocationManager.getRootInterceptor(targetObject).registerAfter(monitor);
        return jexxaMain.registerHealthCheck(monitor);
    }

    /**
     * @deprecated Use typed method {@link #with(AroundMonitor)}
     */
    @Deprecated(forRemoval = true)
    public <U extends HealthCheck & AroundInterceptor> JexxaMain aroundCalls(U monitor)
    {
        InvocationManager.getRootInterceptor(targetObject).registerAround(monitor);
        return jexxaMain.registerHealthCheck(monitor);
    }

    public JexxaMain with(BeforeMonitor monitor)
    {
        monitor.setObservedObject(targetObject);
        InvocationManager.getRootInterceptor(targetObject).registerBefore(monitor);
        return jexxaMain.registerHealthCheck(monitor);
    }

    public JexxaMain with(AfterMonitor monitor)
    {
        InvocationManager.getRootInterceptor(targetObject).registerAfter(monitor);
        return jexxaMain.registerHealthCheck(monitor);
    }

    public JexxaMain with(AroundMonitor monitor)
    {
        InvocationManager.getRootInterceptor(targetObject).registerAround(monitor);
        return jexxaMain.registerHealthCheck(monitor);
    }
}
