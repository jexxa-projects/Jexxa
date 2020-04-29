package io.jexxa.application.infrastructure.drivenadapter.factory;

import java.util.Properties;

import io.jexxa.application.domainservice.IFactoryMethodService;

@SuppressWarnings("unused")
public class FactoryMethodAdapter implements IFactoryMethodService
{
    private FactoryMethodAdapter()
    {
        
    }


    private FactoryMethodAdapter(Properties properties)
    {

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
