package io.jexxa.jexxatest.integrationtest.rest;

import kong.unirest.HttpResponse;

public class BadRequestException extends RuntimeException
{
    public BadRequestException(HttpResponse<?> httpResponse) {
        super("Unknown HTTP URL : " + httpResponse.getRequestSummary().getUrl());
    }
}
