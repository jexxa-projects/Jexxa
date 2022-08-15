package io.jexxa.application.infrastructure.drivingadapter.generic;

import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyDrivingAdapter implements IDrivingAdapter
{
    private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

    private final List<Object> portList = new ArrayList<>();

    public ProxyDrivingAdapter()
    {
        incrementInstanceCount();
    }

    @Override
    public void register(Object port)
    {
        portList.add(port);
    }

    @Override
    public void start()
    {
        // No operation required
    }

    @Override
    public void stop()
    {
        // No operation required
    }

    public List<Object> getPortList()
    {
        return portList;
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
