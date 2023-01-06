package io.jexxa.jexxatest.infrastructure.integrationtest.rest;

import io.jexxa.utils.json.JSONManager;
import kong.unirest.GenericType;
import kong.unirest.ObjectMapper;

public class UnirestObjectMapper implements ObjectMapper
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