package io.jexxa.testapplication.infrastructure.drivenadapter.factory;


import io.jexxa.testapplication.domainservice.InvalidConstructorService;

@SuppressWarnings("unused")
public class InvalidConstructorServiceImpl implements InvalidConstructorService
{
    InvalidConstructorServiceImpl()
    {
        //Invalid Adapter because constructor is package private
    }
}
