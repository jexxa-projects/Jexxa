package io.jexxa.testapplication.applicationservice;

import io.jexxa.testapplication.annotation.InvalidApplicationService;

@InvalidApplicationService
public class InvalidConstructorApplicationService
{
    /**
     * Does not provide a public constructor for testing violation of conventions
     */
    InvalidConstructorApplicationService()
    {
        //Empty constructor since class is for testing purpose only
    }
}
