package io.ddd.jexxa.application.applicationservice;

import io.ddd.jexxa.application.annotation.ApplicationService;
import io.ddd.jexxa.application.domainservice.IDefaultConstructorService;
import io.ddd.jexxa.application.domainservice.IFactoryMethodService;
import io.ddd.jexxa.application.domainservice.IPropertiesConstructorService;

@SuppressWarnings("unused")
@ApplicationService
public class ApplicationServiceWithDrivenAdapters
{
    public ApplicationServiceWithDrivenAdapters(
            IDefaultConstructorService defaultConstructorService,
            IFactoryMethodService factoryMethodService,
            IPropertiesConstructorService propertiesConstructorService
    )
    {

    }
}
