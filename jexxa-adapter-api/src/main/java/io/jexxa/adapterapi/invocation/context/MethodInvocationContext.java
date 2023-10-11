package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;
import io.jexxa.adapterapi.invocation.InvocationTargetRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class MethodInvocationContext extends InvocationContext {
    private final Method method;
    private final Object object;
    private final Object[] args;

    private Object returnValue;

    public MethodInvocationContext(Method method, Object object, Object[] args, List<AroundInterceptor> interceptors)
    {
        super(object,interceptors);
        this.method = Objects.requireNonNull( method );
        this.object = Objects.requireNonNull( object );
        this.args = Objects.requireNonNull( args );
    }

    @Override
    public void invoke()
    {
        try {
            returnValue = method.invoke(object, args);
        } catch (InvocationTargetException e)
        {
            throw new InvocationTargetRuntimeException(e.getTargetException());
        } catch (IllegalAccessException e)
        {
            throw new InvocationTargetRuntimeException(e);
        }
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArgs() {
        return args;
    }

    public Object getReturnValue() {
        return returnValue;
    }
}
