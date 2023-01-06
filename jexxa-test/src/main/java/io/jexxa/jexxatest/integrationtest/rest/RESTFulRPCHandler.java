package io.jexxa.jexxatest.integrationtest.rest;

import kong.unirest.GenericType;
import kong.unirest.Unirest;

import java.util.Properties;
import java.util.stream.Stream;

import static kong.unirest.ContentType.APPLICATION_JSON;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

public class RESTFulRPCHandler
{
    private static final String JEXXA_REST_PORT = "io.jexxa.rest.port";
    private static final String JEXXA_REST_HTTPS_PORT = "io.jexxa.rest.https_port";

    private final String restPrefix;

    public RESTFulRPCHandler(Properties properties, Class<?> endpointClazz)
    {
        this.restPrefix = getRestPrefix(properties, endpointClazz);
    }

    public <T> T getRequest(Class<T> returnType, String method)
    {
        return Unirest.get(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .asObject(returnType)
                .getBody();
    }

    public <T> T getRequest(GenericType<T> genericReturnType, String method)
    {
        return Unirest.get(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .asObject(genericReturnType)
                .getBody();
    }

    public <T> T postRequest(Class<T> returnType, String method, Object parameter)
    {
        return Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(parameter)
                .asObject(returnType).getBody();
    }

    public <T> T postRequest(Class<T> returnType, String method, Object... parameters)
    {
        return Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(Stream.of(parameters).toArray())
                .asObject(returnType).getBody();
    }

    protected static String getRestPrefix(Properties properties, Class<?> clazz)
    {
        if (properties.containsKey(JEXXA_REST_PORT)){
            return "http://localhost:" + properties.getProperty(JEXXA_REST_PORT) + "/" + clazz.getSimpleName() + "/";
        }

        if (properties.containsKey(JEXXA_REST_HTTPS_PORT)){
            return "https://localhost:" + properties.getProperty(JEXXA_REST_HTTPS_PORT) + "/" + clazz.getSimpleName() + "/";
        }

        throw new IllegalArgumentException("Properties do not contain valid HTTP/HTTPS configuration");
    }
}
