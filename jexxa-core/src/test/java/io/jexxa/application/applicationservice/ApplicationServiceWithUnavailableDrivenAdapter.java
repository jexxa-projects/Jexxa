package io.jexxa.application.applicationservice;

import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.domainservice.INotImplementedService;

@SuppressWarnings("unused")
@ApplicationService
public class ApplicationServiceWithUnavailableDrivenAdapter
{
    public ApplicationServiceWithUnavailableDrivenAdapter(INotImplementedService notImplementedService)
    {
        //Empty constructor since class is for testing purpose only
    }
}
