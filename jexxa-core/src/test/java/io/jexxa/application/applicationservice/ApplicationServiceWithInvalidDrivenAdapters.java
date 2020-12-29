package io.jexxa.application.applicationservice;

import java.util.Objects;

import io.jexxa.application.domainservice.IInvalidService;

public class ApplicationServiceWithInvalidDrivenAdapters
{
    public ApplicationServiceWithInvalidDrivenAdapters(IInvalidService invalidService)
    {
        Objects.requireNonNull(invalidService);
    }
}
