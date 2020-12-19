package io.jexxa.application.applicationservice;

import java.util.Objects;

import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.domainservice.IDefaultConstructorService;
import io.jexxa.application.domainservice.IFactoryMethodService;
import io.jexxa.application.domainservice.IPropertiesConstructorService;

@ApplicationService
public class ApplicationServiceWithDrivenAdapters
{
    public ApplicationServiceWithDrivenAdapters(
            IDefaultConstructorService defaultConstructorService,
            IFactoryMethodService factoryMethodService,
            IPropertiesConstructorService propertiesConstructorService
    )
    {
        Objects.requireNonNull(defaultConstructorService);
        Objects.requireNonNull(factoryMethodService);
        Objects.requireNonNull(propertiesConstructorService);
    }
}
