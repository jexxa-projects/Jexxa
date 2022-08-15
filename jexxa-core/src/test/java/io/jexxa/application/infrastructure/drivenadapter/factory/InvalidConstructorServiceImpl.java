package io.jexxa.application.infrastructure.drivenadapter.factory;


import io.jexxa.application.domainservice.InvalidConstructorService;

@SuppressWarnings("unused")
public class InvalidConstructorServiceImpl implements InvalidConstructorService
{
    InvalidConstructorServiceImpl()
    {
        //Invalid Adapter because constructor is package private
    }
}
