package io.jexxa.jexxatest.integrationtest.rest;

import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;
import static kong.unirest.ContentType.APPLICATION_JSON;
import static kong.unirest.HeaderNames.CONTENT_TYPE;

public class RESTHandler
{
    private static final String JEXXA_REST_PORT = "io.jexxa.rest.port";
    private static final String JEXXA_REST_HTTPS_PORT = "io.jexxa.rest.https.port";

    private final String restPrefix;

    public RESTHandler(Properties properties, Class<?> endpointClazz)
    {
        this.restPrefix = getRestPrefix(properties, endpointClazz);
    }


    public <T> T getRequest(Class<T> returnType, String method)
    {
        return internalGet(returnType, method)
                .ifFailure(this::throwUncheckedException)
                .getBody();
    }

    public <T> T getRequest(GenericType<T> genericReturnType, String method)
    {
        return internalGet(genericReturnType, method)
                .ifFailure(this::throwUncheckedException)
                .getBody();
    }

    public <T> T throwingGetRequest(Class<T> returnType, String method) throws Exception
    {
        var response = internalGet(returnType, method);

        if (response.isSuccess()){
            return response.getBody();
        }

        throwCheckedException(response);
        throwUncheckedException(response);
        return null;
    }


    public <T> T throwingGetRequest(GenericType<T> genericReturnType, String method) throws Exception
    {
        var response = internalGet(genericReturnType, method);

        if (response.isSuccess()){
            return response.getBody();
        }

        throwCheckedException(response);
        throwUncheckedException(response);
        return null;
    }


    public <T> T postRequest(Class<T> returnType, String method, Object parameter)
    {
        return Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(parameter)
                .asObject(returnType)
                .ifFailure(this::throwUncheckedException)
                .getBody();
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T> T postRequest(Class<T> returnType, String method, Object... parameters)
    {
        return Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(Stream.of(parameters).toArray())
                .asObject(returnType)
                .ifFailure(this::throwUncheckedException)
                .getBody();
    }

    public <T> T throwingPostRequest(Class<T> returnType, String method, Object parameter) throws Exception
    {
        var response = Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(parameter)
                .asObject(returnType);

        if (response.isSuccess()){
            return response.getBody();
        }

        throwCheckedException(response);
        throwUncheckedException(response);
        return null;
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T> T throwingPostRequest(Class<T> returnType, String method, Object... parameters) throws Exception {
        var response = Unirest.post(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .body(Stream.of(parameters).toArray())
                .asObject(returnType);

        if (response.isSuccess()){
            return response.getBody();
        }

        throwCheckedException(response);
        throwUncheckedException(response);
        return null;
    }


    @SuppressWarnings("unchecked")
    private void throwCheckedException(HttpResponse<?> httpResponse) throws Exception
    {
        var exceptionWrapper = httpResponse.mapError(ExceptionWrapper.class);

        if (exceptionWrapper == null)
        {
            throw new BadRequestException(httpResponse);
        }

        try {
            Class<?> clazz = Class.forName(exceptionWrapper.ExceptionType);

            if (Exception.class.isAssignableFrom(clazz)) {
                Class<? extends Exception> exceptionClass = (Class<? extends Exception>) clazz;
                throw getJSONConverter().fromJson(exceptionWrapper.Exception, exceptionClass);
            }
        } catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException(exceptionWrapper.Exception, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void throwUncheckedException(HttpResponse<T> httpResponse)
    {
        var exceptionWrapper = httpResponse.mapError(ExceptionWrapper.class);

        if (exceptionWrapper == null)
        {
            throw new BadRequestException(httpResponse);
        }

        try {
            Class<?> clazz = Class.forName(exceptionWrapper.ExceptionType);

            if (RuntimeException.class.isAssignableFrom(clazz)) {
                Class<? extends RuntimeException> exceptionClass = (Class<? extends RuntimeException>) clazz;
                throw getJSONConverter().fromJson(exceptionWrapper.Exception, exceptionClass);
            }
        } catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException(exceptionWrapper.Exception, e);
        }
        throw new IllegalArgumentException(exceptionWrapper.Exception);
    }

    private <T> HttpResponse<T> internalGet(GenericType<T> genericReturnType, String method)
    {
        return Unirest.get(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .asObject(genericReturnType);
    }
    private <T> HttpResponse<T> internalGet(Class<T> returnType, String method)
    {
        return Unirest.get(restPrefix + method)
                .header(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                .asObject(returnType);
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
