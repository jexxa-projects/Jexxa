package io.jexxa.application.infrastructure.drivenadapter.factory;

import java.util.Properties;

import io.jexxa.application.domainservice.IInvalidAdapterProperties;
import org.apache.commons.lang3.Validate;

/**
 * Throws an IllegalArgumentException in constructor to simulate invalid properties
 */
@SuppressWarnings("unused")
public class InvalidPropertiesAdapter implements IInvalidAdapterProperties
{
    public InvalidPropertiesAdapter(Properties properties)
    {
        Validate.notNull(properties);
        throw new IllegalArgumentException("InvalidAdapterProperties test");
    }
}
