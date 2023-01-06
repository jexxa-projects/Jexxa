package io.jexxa.jexxatest.infrastructure.rest;

import io.jexxa.jexxatest.JexxaIntegrationTest;
import io.jexxa.utils.JexxaLogger;
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

    public <T> T getRequest(String method, Class<T> returnType)
    {
        JexxaLogger.getLogger(JexxaIntegrationTest.class).error("Invoke method: " + restPrefix + method);

        return Unirest.get(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .asObject(returnType)
                .getBody();
    }

    public <T> T postRequest(String method, Class<T> returnType, Object parameter)
    {
        JexxaLogger.getLogger(JexxaIntegrationTest.class).error("Invoke method: " + restPrefix + method);

        return Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(parameter)
                .asObject(returnType).getBody();
    }

    public <T> T postRequest(String method, Class<T> returnType, Object... parameters)
    {
        JexxaLogger.getLogger(JexxaIntegrationTest.class).error("Invoke method: " + restPrefix + method);

        return Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(Stream.of(parameters).toArray())
                .asObject(returnType).getBody();
    }

    private static String getRestPrefix(Properties properties, Class<?> clazz)
    {
        if (properties.containsKey(JEXXA_REST_PORT)){
            return "http://localhost:" + properties.getProperty(JEXXA_REST_PORT) + "/" + clazz.getSimpleName() + "/";
        }

        if (properties.containsKey(JEXXA_REST_HTTPS_PORT)){
            return "https://localhost:" + properties.getProperty(JEXXA_REST_HTTPS_PORT) + "/" + clazz.getSimpleName() + "/";
        }

        properties.forEach((key, value)-> JexxaLogger.getLogger(JexxaIntegrationTest.class).error(key +" -> " + value));

        throw new IllegalArgumentException("Properties do not contain valid HTTP/HTTPS configuration");
    }
}
