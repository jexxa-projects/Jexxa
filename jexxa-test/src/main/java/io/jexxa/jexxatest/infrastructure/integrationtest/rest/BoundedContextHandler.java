package io.jexxa.jexxatest.infrastructure.integrationtest.rest;

import io.jexxa.adapterapi.drivingadapter.Diagnostics;
import io.jexxa.core.VersionInfo;
import kong.unirest.GenericType;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class BoundedContextHandler extends RESTFulRPCHandler
{
    public BoundedContextHandler(Properties properties, Class<?> endpointClazz)
    {
        super(properties, endpointClazz);
    }

    public Duration uptime()
    {
        return getRequest("uptime", Duration.class);
    }

    public String contextName()
    {
        return getRequest("contextName", String.class);
    }

    public VersionInfo jexxaVersion()
    {
        return getRequest("jexxaVersion", VersionInfo.class);
    }

    public VersionInfo contextVersion()
    {
        return getRequest("contextVersion", VersionInfo.class);
    }

    public boolean isRunning()
    {
        return getRequest("isRunning", Boolean.class);
    }

    public boolean isHealthy()
    {
        return getRequest("isHealthy", Boolean.class);
    }

    public List<Diagnostics> diagnostics()
    {
        return getRequest("diagnostics", new GenericType<>() {});
    }
}
