package io.jexxa.application.infrastructure.drivenadapter.factory;

import io.jexxa.application.domainservice.IInvalidConstructor;

@SuppressWarnings("unused")
public class InvalidConstructor implements IInvalidConstructor
{
    public InvalidConstructor(Integer integer)
    {
        //Empty constructor to check invalid parameter handling
    }
}
