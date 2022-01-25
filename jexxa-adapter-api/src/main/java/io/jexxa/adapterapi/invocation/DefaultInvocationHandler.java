package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.interceptor.AfterInterceptor;
import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.interceptor.BeforeInterceptor;
import io.jexxa.adapterapi.invocation.function.SerializableConsumer;
import io.jexxa.adapterapi.invocation.function.SerializableFunction;
import io.jexxa.adapterapi.invocation.function.SerializableRunnable;
import io.jexxa.adapterapi.invocation.context.ConsumerInvocationContext;
import io.jexxa.adapterapi.invocation.context.FunctionInvocationContext;
import io.jexxa.adapterapi.invocation.context.MethodInvocationContext;
import io.jexxa.adapterapi.invocation.context.RunnableInvocationContext;
import io.jexxa.adapterapi.invocation.context.SupplierInvocationContext;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DefaultInvocationHandler implements AroundInterceptor, BeforeInterceptor, AfterInterceptor, InvocationHandler {
    private final List<BeforeInterceptor> beforeList = new ArrayList<>();
    private final List<AfterInterceptor> afterList = new ArrayList<>();
    private final List<AroundInterceptor> aroundList = new ArrayList<>();

    private static final Object GLOBAL_SYNCHRONIZATION_OBJECT = new Object();

    @Override
    public void before(InvocationContext invocationContext)
    {
        beforeList.forEach(element -> element.before(invocationContext));
    }

    @Override
    public void after(InvocationContext invocationContext)
    {
        afterList.forEach(element -> element.after(invocationContext));
    }

    @Override
    public void around(InvocationContext invocationContext) throws InvocationTargetException, IllegalAccessException
    {
        invocationContext.proceed();
    }

    public DefaultInvocationHandler registerAround(AroundInterceptor interceptor) {
        aroundList.add(interceptor);
        return this;
    }

    public DefaultInvocationHandler registerBefore(BeforeInterceptor interceptor) {
        beforeList.add(interceptor);
        return this;
    }

    public DefaultInvocationHandler registerAfter(AfterInterceptor interceptor) {
        afterList.add(interceptor);
        return this;
    }


    @Override
    public Object invoke(Method method, Object object, Object[] args) throws InvocationTargetException, IllegalAccessException {
        var invocationContext = new MethodInvocationContext(method, object, args, aroundList);

        invoke(invocationContext);

        return invocationContext.getReturnValue();
    }

    @Override
    public void invoke(Object targetObject, SerializableRunnable runnable)
    {
        try {
            var invocationContext = new RunnableInvocationContext(targetObject, runnable, aroundList);
            invoke(invocationContext);
        } catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> void invoke(Object targetObject,SerializableConsumer<T> consumer, T argument)
    {
        try {
            var invocationContext = new ConsumerInvocationContext<>(targetObject, consumer, argument, aroundList);
            invoke(invocationContext);
        } catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T invoke(Object targetObject,SerializableSupplier<T> supplier) {
        try {
            var invocationContext = new SupplierInvocationContext<>(targetObject, supplier, aroundList);
            invoke(invocationContext);
            return invocationContext.getReturnValue();
        } catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T, R> R invoke(Object targetObject,SerializableFunction<T, R> function, T argument) {
        try {
            var invocationContext = new FunctionInvocationContext<>(targetObject, function, argument, aroundList);
            invoke(invocationContext);
            return invocationContext.getReturnValue();
        } catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    protected void invoke(InvocationContext invocationContext) throws InvocationTargetException, IllegalAccessException {
        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            before(invocationContext);
            around(invocationContext);
            after(invocationContext);
        }
    }
}
