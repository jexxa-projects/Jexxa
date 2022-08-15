package io.jexxa.application.infrastructure.drivenadapter.factory;

import io.jexxa.application.domainservice.InvalidConstructorParameterService;

@SuppressWarnings("unused")
public class InvalidConstructorParameterServiceImpl implements InvalidConstructorParameterService
{
    public InvalidConstructorParameterServiceImpl(Integer integer)
    {
        //Empty constructor to check invalid parameter handling
    }
}
