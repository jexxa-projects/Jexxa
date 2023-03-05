package io.jexxa.testapplication.applicationservice;

import io.jexxa.testapplication.annotation.ValidApplicationService;
import io.jexxa.testapplication.domainservice.ValidDefaultConstructorService;
import io.jexxa.testapplication.domainservice.ValidFactoryMethodService;
import io.jexxa.testapplication.domainservice.ValidPropertiesConstructorService;

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
