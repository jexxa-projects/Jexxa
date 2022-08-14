package io.jexxa.application.infrastructure.drivenadapter.factory;

import io.jexxa.application.domainservice.FactoryMethodService;

import java.util.Objects;
import java.util.Properties;

/**
 * Simulate a valid driven adapter with static factory method expecting a Properties
 */
@SuppressWarnings("unused")
public final class FactoryMethodServiceImpl implements FactoryMethodService
{
    private FactoryMethodServiceImpl()
    {
        //Empty and private constructor so that static methods must be used in tests
    }


    private FactoryMethodServiceImpl(Properties properties)
    {
        Objects.requireNonNull(properties);
    }

    public static FactoryMethodService create()
    {
        return new FactoryMethodServiceImpl();
    }

    public static FactoryMethodService create(Properties properties)
    {
        return new FactoryMethodServiceImpl(properties);
    }

}
