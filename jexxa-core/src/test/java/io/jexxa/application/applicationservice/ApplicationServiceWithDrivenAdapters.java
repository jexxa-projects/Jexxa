package io.jexxa.application.applicationservice;

import io.jexxa.application.annotation.ValidApplicationService;
import io.jexxa.application.domainservice.ValidDefaultConstructorService;
import io.jexxa.application.domainservice.ValidFactoryMethodService;
import io.jexxa.application.domainservice.ValidPropertiesConstructorService;

import java.util.Objects;

@ValidApplicationService
public class ApplicationServiceWithDrivenAdapters
{
    public ApplicationServiceWithDrivenAdapters(
            ValidDefaultConstructorService validDefaultConstructorService,
            ValidFactoryMethodService validFactoryMethodService,
            ValidPropertiesConstructorService validPropertiesConstructorService
    )
    {
        Objects.requireNonNull(validDefaultConstructorService);
        Objects.requireNonNull(validFactoryMethodService);
        Objects.requireNonNull(validPropertiesConstructorService);
    }
}
