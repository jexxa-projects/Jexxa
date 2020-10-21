package io.jexxa.infrastructure.drivingadapter.rest.openapi;

@SuppressWarnings({"java:S1104", "java:S116"})
public class BadRequestResponse
{
    public String Exception;
    public String ExceptionType;
    public String ApplicationType = "application/json";
    BadRequestResponse()
    {
        //private constructor
    }
}
