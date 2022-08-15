package io.jexxa.application.infrastructure.drivingadapter.portadapter;

import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;

public record PortAdapter(ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters)
{
    public ApplicationServiceWithDrivenAdapters getPort() {
        return applicationServiceWithDrivenAdapters;
    }
}
