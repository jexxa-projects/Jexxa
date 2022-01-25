package io.jexxa.adapterapi.invocation.context;

import io.jexxa.adapterapi.interceptor.AroundInterceptor;
import io.jexxa.adapterapi.invocation.InvocationContext;

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
        super(interceptors);
        this.method = Objects.requireNonNull( method );
        this.object = Objects.requireNonNull( object );
        this.args = Objects.requireNonNull( args );
    }

    /**
     * This method performs a method invocation on given method.
     *
     * @throws InvocationTargetException forwards exception from Java's reflective API because it cannot be handled here in a meaningful way
     */
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        returnValue = method.invoke(object, args);
    }

    public Method getMethod() {
        return method;
    }

    public Object getTarget() {
        return object;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getReturnValue() {
        return returnValue;
    }
}
