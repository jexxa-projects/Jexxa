package io.jexxa.testapplication.infrastructure.drivingadapter.portadapter;

import io.jexxa.testapplication.applicationservice.SimpleApplicationService;

import java.util.Objects;

public class ThrowingPortAdapter {
    public ThrowingPortAdapter(SimpleApplicationService simpleApplicationService)
    {
        Objects.requireNonNull(simpleApplicationService);
        throw new IllegalStateException("Illegal State");
    }
}
