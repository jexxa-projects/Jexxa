package io.ddd.jexxa.core;

import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.apache.commons.lang.Validate;

public class Jexxa
{
    public void bind(Class<? extends IDrivingAdapter> adapter, Class<?> port) {
        ClassFactory classFactory = new ClassFactory();
        var drivingAdapter = classFactory.createByConstructor(adapter);
        Validate.notNull(drivingAdapter);

        var inboundPort = classFactory.createByConstructor(port);
        Validate.notNull(inboundPort);
        drivingAdapter.register(inboundPort);
    }
}
