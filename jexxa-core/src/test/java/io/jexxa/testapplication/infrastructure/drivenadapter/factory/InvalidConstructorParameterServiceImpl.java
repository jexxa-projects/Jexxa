package io.jexxa.testapplication.infrastructure.drivenadapter.factory;

import io.jexxa.testapplication.domainservice.InvalidConstructorParameterService;

@SuppressWarnings("unused")
public class InvalidConstructorParameterServiceImpl implements InvalidConstructorParameterService
{
    public InvalidConstructorParameterServiceImpl(Integer integer)
    {
        //Empty constructor to check invalid parameter handling
    }
}
