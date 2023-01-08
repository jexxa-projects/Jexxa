package io.jexxa.jexxatest.integrationtest.rest;

import io.jexxa.adapterapi.drivingadapter.Diagnostics;
import io.jexxa.core.VersionInfo;
import kong.unirest.GenericType;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class BoundedContextHandler extends RESTHandler
{
    public BoundedContextHandler(Properties properties, Class<?> endpointClazz)
    {
        super(properties, endpointClazz);
    }

    public Duration uptime()
    {
        return getRequest(Duration.class, "uptime");
    }

    public String contextName()
    {
        return getRequest(String.class, "contextName");
    }

    public VersionInfo jexxaVersion()
    {
        return getRequest(VersionInfo.class, "jexxaVersion");
    }

    public VersionInfo contextVersion()
    {
        return getRequest(VersionInfo.class, "contextVersion");
    }

    public boolean isRunning()
    {
        return getRequest(Boolean.class, "isRunning");
    }

    public boolean isHealthy()
    {
        return getRequest(Boolean.class, "isHealthy");
    }

    public List<Diagnostics> diagnostics()
    {
        return getRequest(new GenericType<>() {}, "diagnostics");
    }
}
