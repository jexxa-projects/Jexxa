package io.jexxa.core;

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
