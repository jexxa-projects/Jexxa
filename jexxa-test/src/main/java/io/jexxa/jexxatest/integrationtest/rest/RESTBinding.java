package io.jexxa.jexxatest.integrationtest.rest;

import io.jexxa.common.facade.json.JSONManager;
import io.jexxa.core.BoundedContext;
import kong.unirest.GenericType;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class RESTBinding implements AutoCloseable
{

    private final Properties properties;

    @SuppressWarnings("java:S1172")
    public RESTBinding(Class<?> ignoredApplication, Properties properties)
    {
        Unirest.config().setObjectMapper(new UnirestObjectMapper());
        this.properties = properties;
        var boundedContext = new BoundedContextHandler(properties, BoundedContext.class);
        await().atMost(10, TimeUnit.SECONDS)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .ignoreException(UnirestException.class)
                .until(boundedContext::isRunning);
    }

    public RESTHandler getRESTHandler(Class<?> endpoint)
    {
        return new RESTHandler(properties, endpoint);
    }

    public BoundedContextHandler getBoundedContext()
    {
        return new BoundedContextHandler(properties, BoundedContext.class);
    }

    @Override
    public void close()   {
        // Nothing to implement here
    }

    private static class UnirestObjectMapper implements ObjectMapper
    {
        @Override
        public <T> T readValue(String value, Class<T> valueType)
        {
            return JSONManager.getJSONConverter().fromJson(value, valueType);
        }

        @Override
        public <T> T readValue(String value, GenericType<T> genericType)
        {
            return JSONManager.getJSONConverter().fromJson(value, genericType.getType());
        }

        @Override
        public String writeValue(Object value)
        {
            return JSONManager.getJSONConverter().toJson(value);
        }
    }
}
