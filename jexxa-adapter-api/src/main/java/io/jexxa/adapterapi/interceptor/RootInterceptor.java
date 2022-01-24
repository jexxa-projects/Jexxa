package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.InvocationHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RootInterceptor implements Interceptor, InvocationHandler {
    private final List<Interceptor> beforeList = new ArrayList<>();
    private final List<Interceptor> afterList = new ArrayList<>();
    private final List<Interceptor> aroundList = new ArrayList<>();

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

    public RootInterceptor register(Interceptor interceptor) {
        beforeList.add(interceptor);
        afterList.add(interceptor);
        aroundList.add(interceptor);
        return this;
    }



    @Override
    public Object invoke(Method method, Object object, Object[] args) throws InvocationTargetException, IllegalAccessException {
        var invocationContext = new InvocationContext(method, object, args, aroundList);

        synchronized (GLOBAL_SYNCHRONIZATION_OBJECT)
        {
            before(invocationContext);
            around(invocationContext);
            after(invocationContext);
        }

        return invocationContext.getReturnValue();

    }
}
