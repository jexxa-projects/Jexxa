package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;

public interface BeforeInterceptor {
    void before(InvocationContext invocationContext);
}
