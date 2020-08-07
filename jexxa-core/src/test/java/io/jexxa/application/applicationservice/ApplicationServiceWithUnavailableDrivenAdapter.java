package io.jexxa.application.applicationservice;

import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.domainservice.INotImplementedService;
import org.apache.commons.lang3.Validate;

@ApplicationService
public class ApplicationServiceWithUnavailableDrivenAdapter
{
    public ApplicationServiceWithUnavailableDrivenAdapter(INotImplementedService notImplementedService)
    {
        Validate.notNull(notImplementedService);
    }
}
