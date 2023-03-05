package io.jexxa.testapplication.infrastructure.drivenadapter.factory;

import io.jexxa.testapplication.domainservice.ValidFactoryMethodService;

import java.util.Objects;
import java.util.Properties;

/**
 * Simulate a valid driven adapter with static factory method expecting a Properties
 */
@SuppressWarnings("unused")
public final class ValidFactoryMethodServiceImpl implements ValidFactoryMethodService
{
    private ValidFactoryMethodServiceImpl()
    {
        //Empty and private constructor so that static methods must be used in tests
    }


    private ValidFactoryMethodServiceImpl(Properties properties)
    {
        Objects.requireNonNull(properties);
    }

    public static ValidFactoryMethodService create()
    {
        return new ValidFactoryMethodServiceImpl();
    }

    public static ValidFactoryMethodService create(Properties properties)
    {
        return new ValidFactoryMethodServiceImpl(properties);
    }

}
