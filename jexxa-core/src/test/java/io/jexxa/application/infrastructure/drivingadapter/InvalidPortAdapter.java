package io.jexxa.application.infrastructure.drivingadapter;

import io.jexxa.application.applicationservice.ThrowingApplicationService;

public class InvalidPortAdapter
{
    private ThrowingApplicationService port;

    public InvalidPortAdapter( ThrowingApplicationService port)
    {
        this.port = port;
    }
}
