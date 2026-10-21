package io.jexxa.testapplication.applicationservice;

import io.jexxa.testapplication.annotation.ValidApplicationService;
import io.jexxa.testapplication.domainservice.DomainEventDispatcher;
import io.jexxa.testapplication.domainservice.ValidDefaultConstructorService;
import io.jexxa.testapplication.domainservice.ValidFactoryMethodService;

import java.util.Objects;

@ValidApplicationService
public class ApplicationServiceWithDomainService {
    public ApplicationServiceWithDomainService(
            ValidDefaultConstructorService validDefaultConstructorService,
            ValidFactoryMethodService validFactoryMethodService,
            DomainEventDispatcher domainEventDispatcher
    )
    {
        Objects.requireNonNull(validDefaultConstructorService);
        Objects.requireNonNull(validFactoryMethodService);
        Objects.requireNonNull(domainEventDispatcher);}

}
