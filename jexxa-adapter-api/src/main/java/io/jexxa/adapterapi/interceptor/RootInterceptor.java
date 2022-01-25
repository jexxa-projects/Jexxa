package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.InvocationHandler;
import io.jexxa.adapterapi.invocation.context.ConsumerInvocationContext;
import io.jexxa.adapterapi.invocation.context.FunctionInvocationContext;
import io.jexxa.adapterapi.invocation.context.MethodInvocationContext;
import io.jexxa.adapterapi.invocation.context.RunnableInvocationContext;
import io.jexxa.adapterapi.invocation.context.SupplierInvocationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RootInterceptor implements AroundInterceptor, BeforeInterceptor, AfterInterceptor, InvocationHandler {
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

    public RootInterceptor registerAround(AroundInterceptor interceptor) {
        aroundList.add(interceptor);
        return this;
    }

    public RootInterceptor registerBefore(BeforeInterceptor interceptor) {
        beforeList.add(interceptor);
        return this;
    }

    public RootInterceptor registerAfter(AfterInterceptor interceptor) {
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
    public void invoke(Object targetObject, JRunnable runnable)
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
    public <T> void invoke(Consumer<T> consumer, T argument)
    {
        try {
            var invocationContext = new ConsumerInvocationContext<>(consumer, argument, aroundList);
            invoke(invocationContext);
        } catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T invoke(Supplier<T> supplier) {
        try {
            var invocationContext = new SupplierInvocationContext<>(supplier, aroundList);
            invoke(invocationContext);
            return invocationContext.getReturnValue();
        } catch (InvocationTargetException | IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T, R> R invoke(Function<T, R> function, T argument) {
        try {
            var invocationContext = new FunctionInvocationContext<>(function, argument, aroundList);
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
