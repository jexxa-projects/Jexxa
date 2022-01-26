package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.function.SerializableSupplier;

import java.lang.reflect.Method;
import java.util.Collection;

public class SupplierInvocationContext<T> extends InvocationContext
{
    private final SerializableSupplier<T> supplier;
    T returnValue;
    Method method;

    public SupplierInvocationContext(Object targetObject,
                                     SerializableSupplier<T> supplier,
                                     Collection<AroundInterceptor> interceptors)
    {
        super(targetObject,interceptors);
        this.supplier = supplier;
    }

    @Override
    public void invoke()
    {
        returnValue = supplier.get();
    }

    @Override
    public Method getMethod() {
        if (method == null)
        {
            method = LambdaUtils.getImplMethod(getTarget(), supplier, getArgTypes());
        }
        return method;
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
