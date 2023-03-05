package io.jexxa.testapplication.infrastructure.drivingadapter.portadapter;

import io.jexxa.testapplication.applicationservice.ApplicationServiceWithDrivenAdapters;

import java.util.Properties;

public record PortAdapterWithProperties(ApplicationServiceWithDrivenAdapters applicationServiceWithDrivenAdapters, Properties properties)
{
    public ApplicationServiceWithDrivenAdapters getPort() {
        return applicationServiceWithDrivenAdapters;
    }
}

