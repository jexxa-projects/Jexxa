package io.jexxa.application.infrastructure.drivenadapter.factory;

import java.util.Objects;
import java.util.Properties;

import io.jexxa.application.domainservice.IInvalidAdapterProperties;

/**
 * Throws an IllegalArgumentException in constructor to simulate invalid properties
 */
@SuppressWarnings("unused")
public class InvalidPropertiesAdapter implements IInvalidAdapterProperties
{
    public InvalidPropertiesAdapter(Properties properties)
    {
        Objects.requireNonNull(properties);
        throw new IllegalArgumentException("InvalidAdapterProperties test");
    }
}
