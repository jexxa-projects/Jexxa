package io.ddd.jexxa.core;

import java.util.Properties;

import io.ddd.jexxa.infrastructure.drivingadapter.CompositeDrivingAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang.Validate;

public class Jexxa
{
    private Properties properties;
    CompositeDrivingAdapter compositeDrivingAdapter;

    public Jexxa(Properties properties)
    {
        this.properties = properties;
        compositeDrivingAdapter = new CompositeDrivingAdapter();
    }

    public void bind(Class<? extends IDrivingAdapter> adapter, Class<?> port) {
        ClassFactory classFactory = new ClassFactory(properties);
        var drivingAdapter = classFactory.createByConstructor(adapter);
        Validate.notNull(drivingAdapter);

        var inboundPort = classFactory.createByConstructor(port);
        Validate.notNull(inboundPort);
        drivingAdapter.register(inboundPort);

        compositeDrivingAdapter.add(drivingAdapter);
    }

    void startDrivingAdapters()
    {
        compositeDrivingAdapter.start();
    }

    void stopDrivingAdapters()
    {
        compositeDrivingAdapter.stop();
    }
}
