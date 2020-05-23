package io.jexxa.application.infrastructure.drivingadapter.messaging;

import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;

public class SimpleApplicationServiceAdapter
{
    private final ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters;

    public SimpleApplicationServiceAdapter(ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters)
    {
        this.applicationServiceWithDrivenAdapters = applicationServiceWithDrivenAdapters;
    }

    public ApplicationServiceWithDrivenAdapters getPort()
    {
        return applicationServiceWithDrivenAdapters;
    }
}
