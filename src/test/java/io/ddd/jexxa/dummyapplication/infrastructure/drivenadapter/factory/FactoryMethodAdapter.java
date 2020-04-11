package io.ddd.jexxa.dummyapplication.infrastructure.drivenadapter.factory;

import java.util.Properties;

import io.ddd.jexxa.dummyapplication.domainservice.IFactoryMethodService;

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
