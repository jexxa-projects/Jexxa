package io.jexxa.core;

import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.InvocationManager;

import java.util.function.Consumer;

public class FluentInterceptor<T>
{
    private final T targetObject;
    private final JexxaMain jexxaMain;

    FluentInterceptor(T targetObject, JexxaMain jexxaMain)
    {
        this.targetObject = targetObject;
        this.jexxaMain = jexxaMain;
    }

    JexxaMain before(Consumer<InvocationContext> consumer)
    {
        InvocationManager
                .getRootInterceptor(targetObject)
                .registerBefore(consumer::accept);

        return jexxaMain;
    }

    FluentInterceptor<T> beforeAnd(Consumer<InvocationContext> consumer)
    {
        before(consumer);
        return this;
    }

    JexxaMain after(Consumer<InvocationContext> consumer)
    {
        InvocationManager
                .getRootInterceptor(targetObject)
                .registerAfter(consumer::accept);

        return jexxaMain;
    }

    FluentInterceptor<T> afterAnd(Consumer<InvocationContext> consumer)
    {
        after(consumer);
        return this;
    }

    JexxaMain around(Consumer<InvocationContext> consumer)
    {
        InvocationManager
                .getRootInterceptor(targetObject)
                .registerAround(consumer::accept);

        return jexxaMain;
    }

    FluentInterceptor<T> aroundAnd(Consumer<InvocationContext> consumer)
    {
        around(consumer);
        return this;
    }
}
