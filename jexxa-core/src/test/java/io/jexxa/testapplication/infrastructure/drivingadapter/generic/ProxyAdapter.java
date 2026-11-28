package io.jexxa.testapplication.infrastructure.drivingadapter.generic;


import io.jexxa.adapters.api.drivingadapter.IDrivingAdapter;
import io.jexxa.adapters.api.invocation.InvocationManager;
import io.jexxa.adapters.api.invocation.function.SerializableConsumer;
import io.jexxa.adapters.api.invocation.function.SerializableFunction;
import io.jexxa.adapters.api.invocation.function.SerializableSupplier;

public class ProxyAdapter implements IDrivingAdapter
{
    private Object port;

    @Override
    public void register(Object port) {
        this.port = port;
    }

    @Override
    public void start() {
        // No action required
    }

    @Override
    public void stop() {
        // No action required
    }

    @SuppressWarnings("unused")
    public <T> void invoke(SerializableConsumer<T> consumer, T argument)
    {
        InvocationManager.getInvocationHandler(port)
                .invoke(port, consumer, argument );
    }

    @SuppressWarnings("unused")
    public <T> T invoke(SerializableSupplier<T> supplier)
    {
        return InvocationManager.getInvocationHandler(port)
                .invoke(port, supplier );
    }

    @SuppressWarnings("unused")
    <T, R> R invoke(SerializableFunction<T, R> function, T argument)
    {
        return InvocationManager.getInvocationHandler(port)
                .invoke(port, function, argument );
    }

}
