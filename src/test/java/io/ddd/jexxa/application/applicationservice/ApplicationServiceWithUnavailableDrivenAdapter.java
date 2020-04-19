package io.ddd.jexxa.application.applicationservice;

import io.ddd.jexxa.application.annotation.ApplicationService;
import io.ddd.jexxa.application.domainservice.INotImplementedService;

@SuppressWarnings("unused")
@ApplicationService
public class ApplicationServiceWithUnavailableDrivenAdapter
{
    public ApplicationServiceWithUnavailableDrivenAdapter(INotImplementedService notImplementedService)
    {

    }
}
