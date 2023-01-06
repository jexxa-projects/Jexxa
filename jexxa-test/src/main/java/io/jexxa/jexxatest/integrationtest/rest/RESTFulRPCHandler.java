package io.jexxa.jexxatest.integrationtest.rest;

import kong.unirest.GenericType;
import kong.unirest.Unirest;

import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;
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
        var response = Unirest.get(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .asObject(returnType);

        if (response.isSuccess()) {
            return response.getBody();
        }

        throw createRuntimeException(response.mapError(ExceptionWrapper.class));
    }

    public <T> T getRequest(GenericType<T> genericReturnType, String method)
    {
        var response = Unirest.get(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .asObject(genericReturnType);

        if (response.isSuccess()) {
            return response.getBody();
        }

        throw createRuntimeException(response.mapError(ExceptionWrapper.class));
    }

    public <T> T postRequest(Class<T> returnType, String method, Object parameter)
    {
        var response = Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(parameter)
                .asObject(returnType);

        if (response.isSuccess()) {
            return response.getBody();
        }

        var exceptionWrapper = response.mapError(ExceptionWrapper.class);
        throw createRuntimeException(exceptionWrapper);
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T> T postRequest(Class<T> returnType, String method, Object... parameters)
    {
        var response = Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(Stream.of(parameters).toArray())
                .asObject(returnType);

        if (response.isSuccess()) {
            return response.getBody();
        }

        var exceptionWrapper = response.mapError(ExceptionWrapper.class);
        throw createRuntimeException(exceptionWrapper);
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T> T postThrowingRequest(Class<T> returnType, String method, Object... parameters) throws Exception {
        var response = Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(Stream.of(parameters).toArray())
                .asObject(returnType);

        if (response.isSuccess()) {
            return response.getBody();
        }

        var exceptionWrapper = response.mapError(ExceptionWrapper.class);
        throwIfTypedException(exceptionWrapper);
        throw createRuntimeException(exceptionWrapper);
    }

    @SuppressWarnings("unchecked")
    private void throwIfTypedException(ExceptionWrapper exceptionWrapper) throws Exception
    {
        try {
            Class<?> clazz = Class.forName(exceptionWrapper.ExceptionType);

            if (Exception.class.isAssignableFrom(clazz)) {
                Class<? extends Exception> exceptionClass = (Class<? extends Exception>) clazz;
                throw getJSONConverter().fromJson(exceptionWrapper.Exception, exceptionClass);
            }
        } catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException(exceptionWrapper.Exception);
        }
    }

    @SuppressWarnings("unchecked")
    private RuntimeException createRuntimeException(ExceptionWrapper exceptionWrapper)
    {
        try {
            Class<?> clazz = Class.forName(exceptionWrapper.ExceptionType);

            if (RuntimeException.class.isAssignableFrom(clazz)) {
                Class<? extends RuntimeException> exceptionClass = (Class<? extends RuntimeException>) clazz;
                return getJSONConverter().fromJson(exceptionWrapper.Exception, exceptionClass);
            }
        } catch (ClassNotFoundException e)
        {
            return new IllegalArgumentException(exceptionWrapper.Exception);
        }
        return new IllegalArgumentException(exceptionWrapper.Exception);
    }

    private static String getRestPrefix(Properties properties, Class<?> clazz)
    {
        if (properties.containsKey(JEXXA_REST_PORT)){
            return "http://localhost:" + properties.getProperty(JEXXA_REST_PORT) + "/" + clazz.getSimpleName() + "/";
        }

        if (properties.containsKey(JEXXA_REST_HTTPS_PORT)){
            return "https://localhost:" + properties.getProperty(JEXXA_REST_HTTPS_PORT) + "/" + clazz.getSimpleName() + "/";
        }

        throw new IllegalArgumentException("Properties do not contain valid HTTP/HTTPS configuration");
    }
    record ExceptionWrapper(String ExceptionType, String Exception, String ApplicationType)
    {
    }

}
