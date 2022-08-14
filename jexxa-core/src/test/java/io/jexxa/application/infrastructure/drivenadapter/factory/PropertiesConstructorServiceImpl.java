package io.jexxa.application.infrastructure.drivenadapter.factory;

import io.jexxa.application.domainservice.PropertiesConstructorService;

import java.util.Objects;
import java.util.Properties;

/**
 * Simulate a valid driven adapter with constructor expecting a Properties
 */
public class PropertiesConstructorServiceImpl implements PropertiesConstructorService
{
    public PropertiesConstructorServiceImpl(Properties properties)
    {
        Objects.requireNonNull(properties);
    }
}
