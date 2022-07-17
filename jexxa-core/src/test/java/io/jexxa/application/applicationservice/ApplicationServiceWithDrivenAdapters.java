package io.jexxa.application.applicationservice;

import io.jexxa.application.annotation.ValidApplicationService;
import io.jexxa.application.domainservice.IDefaultConstructorService;
import io.jexxa.application.domainservice.IFactoryMethodService;
import io.jexxa.application.domainservice.IPropertiesConstructorService;

import java.util.Objects;

@ValidApplicationService
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
