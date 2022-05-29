package io.jexxa.core;

import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.InvocationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class FluentInterceptor
{
    private final Collection<Object> targetObjects = new ArrayList<>();
    private final JexxaMain jexxaMain;

    FluentInterceptor(JexxaMain jexxaMain, Object... targetObjects )
    {
        this.targetObjects.addAll(Arrays.asList(targetObjects));

        this.jexxaMain = jexxaMain;
    }

    public JexxaMain before(Consumer<InvocationContext> consumer)
    {
        targetObjects.stream()
                .map(InvocationManager::getRootInterceptor)
                .forEach( interceptor -> interceptor.registerBefore(consumer::accept));

        return jexxaMain;
    }

    public FluentInterceptor beforeAnd(Consumer<InvocationContext> consumer)
    {
        before(consumer);
        return this;
    }

    public JexxaMain after(Consumer<InvocationContext> consumer)
    {
        targetObjects.stream()
                .map(InvocationManager::getRootInterceptor)
                .forEach( interceptor -> interceptor.registerAfter(consumer::accept));

        return jexxaMain;
    }

    public FluentInterceptor afterAnd(Consumer<InvocationContext> consumer)
    {
        after(consumer);
        return this;
    }

    public JexxaMain around(Consumer<InvocationContext> consumer)
    {
        targetObjects.stream()
                .map(InvocationManager::getRootInterceptor)
                .forEach( interceptor -> interceptor.registerAround(consumer::accept));

        return jexxaMain;
    }

    public FluentInterceptor aroundAnd(Consumer<InvocationContext> consumer)
    {
        around(consumer);
        return this;
    }
}
