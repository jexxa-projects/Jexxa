package io.ddd.jexxa.dummyapplication.applicationservice;

import io.ddd.jexxa.dummyapplication.domainservice.INotImplementedService;
import io.ddd.jexxa.dummyapplication.annotation.*;

@SuppressWarnings("unused")
@ApplicationService
public class ApplicationServiceWithUnavailableDrivenAdapter
{
    public ApplicationServiceWithUnavailableDrivenAdapter(INotImplementedService notImplementedService)
    {

    }
}
