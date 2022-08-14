package io.jexxa.application.infrastructure.drivenadapter.factory;

import io.jexxa.application.domainservice.InvalidConstructor;

@SuppressWarnings("unused")
public class InvalidConstructorImpl implements InvalidConstructor
{
    public InvalidConstructorImpl(Integer integer)
    {
        //Empty constructor to check invalid parameter handling
    }
}
