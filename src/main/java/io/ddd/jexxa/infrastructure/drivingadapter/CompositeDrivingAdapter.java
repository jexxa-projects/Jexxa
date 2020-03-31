package io.ddd.jexxa.infrastructure.drivingadapter;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;

public class CompositeDrivingAdapter implements IDrivingAdapter
{
    private final Set<IDrivingAdapter> drivingAdapters = new HashSet<>();

    @Override
    public void start()
    {
        drivingAdapters.forEach(IDrivingAdapter::start);
    }

    @Override
    public void stop()
    {
        drivingAdapters.forEach(IDrivingAdapter::stop);
    }

    @Override
    public void register(Object object)
    {
        Validate.notNull(object);
        drivingAdapters.forEach(element -> element.register(object));
    }

    public void add(IDrivingAdapter drivingAdapter)
    {
        Validate.notNull(drivingAdapter);
        drivingAdapters.add(drivingAdapter);
    }

}
