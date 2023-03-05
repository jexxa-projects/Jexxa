package io.jexxa.testapplication.infrastructure.drivenadapter.factory;

import io.jexxa.testapplication.domainservice.ValidPropertiesConstructorService;

import java.util.Objects;
import java.util.Properties;

/**
 * Simulate a valid driven adapter with constructor expecting a Properties
 */
public class ValidPropertiesConstructorServiceImpl implements ValidPropertiesConstructorService
{
    public ValidPropertiesConstructorServiceImpl(Properties properties)
    {
        Objects.requireNonNull(properties);
    }
}
