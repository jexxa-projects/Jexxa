package io.jexxa.application.infrastructure.drivenadapter.factory;

import java.util.Properties;

import io.jexxa.application.domainservice.IPropertiesConstructorService;
import org.apache.commons.lang3.Validate;

public class PropertiesConstructorAdapter implements IPropertiesConstructorService
{
    public PropertiesConstructorAdapter(Properties properties)
    {
        Validate.notNull(properties);
    }
}
