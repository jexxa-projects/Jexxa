package io.jexxa.application.infrastructure.drivingadapter;

import io.jexxa.application.applicationservice.SimpleApplicationService;

@SuppressWarnings("unused")
public class InvalidPortAdapter
{
    public InvalidPortAdapter( SimpleApplicationService port)
    {
        throw new IllegalArgumentException("Simulate exception in constructor of a PortAdapter.");
    }
}
