package io.jexxa.application.applicationservice;

public class ThrowingApplicationService
{
    public ThrowingApplicationService()
    {
        throw new IllegalArgumentException("Simulate exception in constructor of a port.");
    }
}
