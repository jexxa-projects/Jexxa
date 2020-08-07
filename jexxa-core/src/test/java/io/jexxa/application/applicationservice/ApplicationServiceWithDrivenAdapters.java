package io.jexxa.application.applicationservice;

import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.domainservice.IDefaultConstructorService;
import io.jexxa.application.domainservice.IFactoryMethodService;
import io.jexxa.application.domainservice.IPropertiesConstructorService;
import org.apache.commons.lang3.Validate;

@ApplicationService
public class ApplicationServiceWithDrivenAdapters
{
    public ApplicationServiceWithDrivenAdapters(
            IDefaultConstructorService defaultConstructorService,
            IFactoryMethodService factoryMethodService,
            IPropertiesConstructorService propertiesConstructorService
    )
    {
        Validate.notNull(defaultConstructorService);
        Validate.notNull(factoryMethodService);
        Validate.notNull(propertiesConstructorService);
    }
}
