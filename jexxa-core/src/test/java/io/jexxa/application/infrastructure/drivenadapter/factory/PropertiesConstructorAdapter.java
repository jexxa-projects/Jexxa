package io.jexxa.application.infrastructure.drivenadapter.factory;

import java.util.Properties;

import io.jexxa.application.domainservice.IPropertiesConstructorService;

@SuppressWarnings("unused")
public class PropertiesConstructorAdapter implements IPropertiesConstructorService
{
    public PropertiesConstructorAdapter(Properties properties)
    {
        //Empty because it is only used for testing purpose
    }
}
