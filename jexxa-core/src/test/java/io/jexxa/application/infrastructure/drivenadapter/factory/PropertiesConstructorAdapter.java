package io.jexxa.application.infrastructure.drivenadapter.factory;

import io.jexxa.application.domainservice.IPropertiesConstructorService;

import java.util.Objects;
import java.util.Properties;

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
