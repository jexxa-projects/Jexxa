package io.jexxa.testapplication.applicationservice;

import io.jexxa.testapplication.domainservice.InvalidConstructorService;

import java.util.Objects;

public class ApplicationServiceWithInvalidDrivenAdapters
{
    public ApplicationServiceWithInvalidDrivenAdapters(InvalidConstructorService invalidConstructorService)
    {
        Objects.requireNonNull(invalidConstructorService);
    }
}
