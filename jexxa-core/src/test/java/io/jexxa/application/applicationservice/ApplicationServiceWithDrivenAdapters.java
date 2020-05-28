package io.jexxa.application.applicationservice;

import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.domainservice.IDefaultConstructorService;
import io.jexxa.application.domainservice.IFactoryMethodService;
import io.jexxa.application.domainservice.IPropertiesConstructorService;

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
        //Empty constructor since class is for testing purpose only
    }
}
