package io.jexxa.application.applicationservice;

import io.jexxa.application.annotation.ValidApplicationService;
import io.jexxa.application.domainservice.DefaultConstructorService;
import io.jexxa.application.domainservice.FactoryMethodService;
import io.jexxa.application.domainservice.PropertiesConstructorService;

import java.util.Objects;

@ValidApplicationService
public class ApplicationServiceWithDrivenAdapters
{
    public ApplicationServiceWithDrivenAdapters(
            DefaultConstructorService defaultConstructorService,
            FactoryMethodService factoryMethodService,
            PropertiesConstructorService propertiesConstructorService
    )
    {
        Objects.requireNonNull(defaultConstructorService);
        Objects.requireNonNull(factoryMethodService);
        Objects.requireNonNull(propertiesConstructorService);
    }
}
