package io.jexxa.application.infrastructure.drivingadapter;

import java.util.concurrent.atomic.AtomicInteger;

import io.jexxa.application.applicationservice.SimpleApplicationService;

public class ProxyPortAdapter
{
    private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

    public ProxyPortAdapter(SimpleApplicationService simpleApplicationService)
    {
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
