package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.invocation.InvocationContext;

public interface AfterInterceptor {
    void after(InvocationContext invocationContext);
}
