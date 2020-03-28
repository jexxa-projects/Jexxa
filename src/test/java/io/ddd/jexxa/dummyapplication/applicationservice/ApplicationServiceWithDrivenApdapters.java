package io.ddd.jexxa.dummyapplication.applicationservice;
       
import io.ddd.jexxa.dummyapplication.domainservice.IDefaultConstructorService;
import io.ddd.jexxa.dummyapplication.domainservice.IFactroyMethodService;
import io.ddd.jexxa.dummyapplication.domainservice.IPropertiesConstructorService;
import io.ddd.jexxa.dummyapplication.annotation.*;

@ApplicationService
public class ApplicationServiceWithDrivenApdapters
{
    public ApplicationServiceWithDrivenApdapters(
            IDefaultConstructorService defaultConstructorService,
            IFactroyMethodService factroyMethodService,
            IPropertiesConstructorService propertiesConstructorService
    )
    {

    }
}
