package io.ddd.jexxa.application.infrastructure.drivingadapter.messaging;

import io.ddd.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;

public class SimpleApplicationServiceWrapper
{
    private final ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters;

    public SimpleApplicationServiceWrapper(ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters)
    {
        this.applicationServiceWithDrivenAdapters = applicationServiceWithDrivenAdapters;
    }

    public ApplicationServiceWithDrivenAdapters getPort()
    {
        return applicationServiceWithDrivenAdapters;
    }
}
