package io.jexxa.testapplication.infrastructure.drivingadapter.portadapter;

import io.jexxa.testapplication.applicationservice.ApplicationServiceWithDrivenAdapters;

public record PortAdapter(ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters)
{
    public ApplicationServiceWithDrivenAdapters getPort() {
        return applicationServiceWithDrivenAdapters;
    }
}
