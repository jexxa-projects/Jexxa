package io.jexxa.application.infrastructure.drivingadapter;

import java.util.ArrayList;
import java.util.List;

import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;

public class ProxyAdapter implements IDrivingAdapter
{
    private final List<Object> portList = new ArrayList<>();

    @Override
    public void register(Object port)
    {
        portList.add(port);
    }

    @Override
    public void start()
    {
        // No opreation required
    }

    @Override
    public void stop()
    {
        // No opreation required
    }

    public List<Object> getPortList()
    {
        return portList;
    }
}
