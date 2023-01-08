package io.jexxa.jexxatest.integrationtest.rest;

import io.jexxa.core.BoundedContext;
import io.jexxa.utils.json.JSONManager;
import kong.unirest.GenericType;
import kong.unirest.ObjectMapper;
import kong.unirest.Unirest;

import java.util.Properties;

public class RESTBinding
{

    private final Properties properties;

    public RESTBinding(Properties properties)
    {
        Unirest.config().setObjectMapper(new UnirestObjectMapper());
        this.properties = properties;
    }

    public RESTHandler getRESTHandler(Class<?> endpoint)
    {
        return new RESTHandler(properties, endpoint);
    }

    public BoundedContextHandler getBoundedContext()
    {
        return new BoundedContextHandler(properties, BoundedContext.class);
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
