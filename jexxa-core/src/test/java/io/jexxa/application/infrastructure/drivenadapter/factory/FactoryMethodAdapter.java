package io.jexxa.application.infrastructure.drivenadapter.factory;

import java.util.Properties;

import io.jexxa.application.domainservice.IFactoryMethodService;
import org.apache.commons.lang3.Validate;

@SuppressWarnings("unused")
public final class FactoryMethodAdapter implements IFactoryMethodService
{
    private FactoryMethodAdapter()
    {
        //Empty and private constructor so that static methods must be used in tests
    }


    private FactoryMethodAdapter(Properties properties)
    {
        Validate.notNull(properties);
    }

    public static IFactoryMethodService create()
    {
        return new FactoryMethodAdapter();
    }

    public static IFactoryMethodService create(Properties properties)
    {
        return new FactoryMethodAdapter(properties);
    }

}
