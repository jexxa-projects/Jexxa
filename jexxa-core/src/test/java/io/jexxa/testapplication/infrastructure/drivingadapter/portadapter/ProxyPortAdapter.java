package io.jexxa.testapplication.infrastructure.drivingadapter.portadapter;

import io.jexxa.testapplication.applicationservice.SimpleApplicationService;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyPortAdapter
{
    private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

    public ProxyPortAdapter(SimpleApplicationService simpleApplicationService)
    {
        Objects.requireNonNull(simpleApplicationService);
        incrementInstanceCount();
    }

    public static int getInstanceCount()
    {
        return INSTANCE_COUNT.get();
    }

    public static void resetInstanceCount()
    {
        INSTANCE_COUNT.set(0);
    }

    private static void incrementInstanceCount()
    {
        INSTANCE_COUNT.incrementAndGet();
    }

}
