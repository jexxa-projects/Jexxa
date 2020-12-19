package io.jexxa.application.infrastructure.drivingadapter;

import java.util.Objects;

import io.jexxa.application.applicationservice.SimpleApplicationService;

public class InvalidPortAdapter
{
    public InvalidPortAdapter( SimpleApplicationService port)
    {
        Objects.requireNonNull(port);
        throw new IllegalArgumentException("Simulate exception in constructor of a PortAdapter.");
    }
}
