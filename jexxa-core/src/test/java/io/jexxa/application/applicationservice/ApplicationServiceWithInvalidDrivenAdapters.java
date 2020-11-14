package io.jexxa.application.applicationservice;

import io.jexxa.application.domainservice.IInvalidService;
import org.apache.commons.lang3.Validate;

public class ApplicationServiceWithInvalidDrivenAdapters
{
    public ApplicationServiceWithInvalidDrivenAdapters(IInvalidService invalidService)
    {
        Validate.notNull(invalidService);
    }
}
