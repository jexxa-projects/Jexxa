package io.jexxa.infrastructure.drivingadapter.rest.openapi;


public class BadRequestResponse
{
    public String Exception;
    public String ExceptionType;
    public final String ApplicationType = "application/json";
    private BadRequestResponse()
    {
        //private constructor
    }
}
