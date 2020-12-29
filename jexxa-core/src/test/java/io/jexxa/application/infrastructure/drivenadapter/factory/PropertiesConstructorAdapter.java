package io.jexxa.application.infrastructure.drivenadapter.factory;

import java.util.Objects;
import java.util.Properties;

import io.jexxa.application.domainservice.IPropertiesConstructorService;

/**
 * Simulate a valid driven adapter with constructor expecting a Properties
 */
public class PropertiesConstructorAdapter implements IPropertiesConstructorService
{
    public PropertiesConstructorAdapter(Properties properties)
    {
        Objects.requireNonNull(properties);
    }
}
