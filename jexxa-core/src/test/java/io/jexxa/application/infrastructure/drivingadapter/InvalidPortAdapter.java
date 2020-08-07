package io.jexxa.application.infrastructure.drivingadapter;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import org.apache.commons.lang3.Validate;

public class InvalidPortAdapter
{
    public InvalidPortAdapter( SimpleApplicationService port)
    {
        Validate.notNull(port);
        throw new IllegalArgumentException("Simulate exception in constructor of a PortAdapter.");
    }
}
