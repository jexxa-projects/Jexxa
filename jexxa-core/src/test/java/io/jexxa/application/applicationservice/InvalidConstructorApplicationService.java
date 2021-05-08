package io.jexxa.application.applicationservice;

import io.jexxa.application.annotation.InvalidApplicationService;

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
