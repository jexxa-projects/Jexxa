package io.jexxa.application.infrastructure.drivingadapter;

import io.jexxa.application.applicationservice.SimpleApplicationService;

import java.util.Objects;

public class InvalidPortAdapter
{
    public InvalidPortAdapter( SimpleApplicationService port)
    {
        Objects.requireNonNull(port);
        throw new IllegalArgumentException("Simulate exception in constructor of a PortAdapter.");
    }
}
