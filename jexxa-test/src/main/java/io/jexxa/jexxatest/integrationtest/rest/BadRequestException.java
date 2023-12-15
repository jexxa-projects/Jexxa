package io.jexxa.jexxatest.integrationtest.rest;

import kong.unirest.HttpResponse;

import java.io.Serial;

public class BadRequestException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException(HttpResponse<?> httpResponse) {
        super("Unknown HTTP URL : " + httpResponse.getRequestSummary().getUrl());
    }
}
