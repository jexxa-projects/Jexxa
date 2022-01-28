package io.jexxa.application.applicationservice;

import io.jexxa.application.domainservice.IInvalidService;

import java.util.Objects;

public class ApplicationServiceWithInvalidDrivenAdapters
{
    public ApplicationServiceWithInvalidDrivenAdapters(IInvalidService invalidService)
    {
        Objects.requireNonNull(invalidService);
    }
}
