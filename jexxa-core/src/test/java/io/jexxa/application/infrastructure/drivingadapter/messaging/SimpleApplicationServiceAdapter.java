package io.jexxa.application.infrastructure.drivingadapter.messaging;

import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;

public record SimpleApplicationServiceAdapter(ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters)
{
    public ApplicationServiceWithDrivenAdapters getPort() {
        return applicationServiceWithDrivenAdapters;
    }
}
