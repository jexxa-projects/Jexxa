package io.jexxa.application.applicationservice;

import io.jexxa.application.domainservice.InvalidConstructorService;

import java.util.Objects;

public class ApplicationServiceWithInvalidDrivenAdapters
{
    public ApplicationServiceWithInvalidDrivenAdapters(InvalidConstructorService invalidConstructorService)
    {
        Objects.requireNonNull(invalidConstructorService);
    }
}
