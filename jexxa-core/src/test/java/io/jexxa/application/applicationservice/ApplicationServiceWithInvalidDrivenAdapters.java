package io.jexxa.application.applicationservice;

import io.jexxa.application.domainservice.InvalidService;

import java.util.Objects;

public class ApplicationServiceWithInvalidDrivenAdapters
{
    public ApplicationServiceWithInvalidDrivenAdapters(InvalidService invalidService)
    {
        Objects.requireNonNull(invalidService);
    }
}
