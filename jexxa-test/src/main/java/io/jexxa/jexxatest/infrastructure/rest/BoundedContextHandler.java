package io.jexxa.jexxatest.infrastructure.rest;

import io.jexxa.core.VersionInfo;

import java.time.Duration;
import java.util.Properties;

public class BoundedContextHandler extends RESTFulRPCHandler
{
    public BoundedContextHandler(Properties properties, Class<?> endpointClazz)
    {
        super(properties, endpointClazz);
    }


    public Duration uptime()
    {
        return getRequest("updime", Duration.class);
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

  /*  public List<Diagnostics> diagnostics()
    {
        return getRequest("diagnostics", List<Diagnostics>.class);
    }*/


}
