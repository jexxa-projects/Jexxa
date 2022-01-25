package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Supplier;

public class SupplierInvocationContext<T> extends InvocationContext
{
    private final Supplier<T> supplier;
    T returnValue;

    public SupplierInvocationContext(Object targetObject, Supplier<T> supplier, Collection<AroundInterceptor> interceptors)
    {
        super(targetObject,interceptors);
        this.supplier = supplier;
    }

    @Override
    public void invoke() throws InvocationTargetException, IllegalAccessException
    {
        returnValue = supplier.get();
    }

    @Override
    public Method getMethod() {
        try {
            return supplier.getClass().getMethod("get");
        } catch (NoSuchMethodException | SecurityException e)
        {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object getTarget() {
        return supplier;
    }

    @Override
    public Object[] getArgs() {
        return new Object[0];
    }

    @Override
    public T getReturnValue() {
        return returnValue;
    }
}
