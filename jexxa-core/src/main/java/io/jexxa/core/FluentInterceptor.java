package io.jexxa.core;

import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.InvocationManager;

import javax.naming.spi.NamingManager;
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
        InvocationManager.getRootInterceptor(targetObject).registerBefore( new BeforeConsumerInterceptor(consumer) );
        return jexxaMain;
    }


    private static class BeforeConsumerInterceptor implements BeforeInterceptor
    {
        Consumer<InvocationContext> consumer;

        public BeforeConsumerInterceptor(Consumer<InvocationContext> consumer)
        {
            this.consumer = consumer;
        }

        @Override
        public void before(InvocationContext invocationContext)
        {
            consumer.accept(invocationContext);
        }
    }

}
