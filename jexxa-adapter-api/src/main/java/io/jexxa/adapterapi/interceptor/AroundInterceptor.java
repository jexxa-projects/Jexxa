package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;

public interface AroundInterceptor  {

    void around(InvocationContext invocationContext );
}
