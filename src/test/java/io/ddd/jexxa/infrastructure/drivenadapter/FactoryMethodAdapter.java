package io.ddd.jexxa.infrastructure.drivenadapter;

import io.ddd.jexxa.applicationcore.domainservice.IFactroyMethodService;

public class FactoryMethodAdapter implements IFactroyMethodService
{
    private FactoryMethodAdapter()
    {
        
    }

    public static IFactroyMethodService create()
    {
        return new FactoryMethodAdapter();
    }
}
