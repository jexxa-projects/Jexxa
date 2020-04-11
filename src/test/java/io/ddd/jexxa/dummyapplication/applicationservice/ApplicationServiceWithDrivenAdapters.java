package io.ddd.jexxa.dummyapplication.applicationservice;
       
import io.ddd.jexxa.dummyapplication.domainservice.IDefaultConstructorService;
import io.ddd.jexxa.dummyapplication.domainservice.IFactoryMethodService;
import io.ddd.jexxa.dummyapplication.domainservice.IPropertiesConstructorService;
import io.ddd.jexxa.dummyapplication.annotation.*;

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
