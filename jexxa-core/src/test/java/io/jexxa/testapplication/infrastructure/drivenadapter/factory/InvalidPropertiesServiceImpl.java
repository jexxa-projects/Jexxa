package io.jexxa.testapplication.infrastructure.drivenadapter.factory;

import io.jexxa.testapplication.domainservice.InvalidPropertiesService;

import java.util.Objects;
import java.util.Properties;

/**
 * Throws an IllegalArgumentException in constructor to simulate invalid properties
 */
@SuppressWarnings("unused")
public class InvalidPropertiesServiceImpl implements InvalidPropertiesService
{
    public InvalidPropertiesServiceImpl(Properties properties)
    {
        Objects.requireNonNull(properties);
        throw new IllegalArgumentException("InvalidAdapterProperties test");
    }
}
