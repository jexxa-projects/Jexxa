package io.jexxa.application.infrastructure.drivenadapter.factory;

import java.util.Properties;

import io.jexxa.application.domainservice.IInvalidAdapterProperties;

@SuppressWarnings("unused")
public class InvalidAdapterProperties implements IInvalidAdapterProperties
{
    public InvalidAdapterProperties(Properties properties)
    {
        throw new IllegalArgumentException("InvalidAdapterProperties test");
    }
}
