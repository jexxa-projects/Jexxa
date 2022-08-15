package io.jexxa.application.infrastructure.drivingadapter.portadapter;

import io.jexxa.application.applicationservice.SimpleApplicationService;

import java.util.Objects;

public class ThrowingPortAdapter {
    public ThrowingPortAdapter(SimpleApplicationService simpleApplicationService)
    {
        Objects.requireNonNull(simpleApplicationService);
        throw new IllegalStateException("Illegal State");
    }
}
