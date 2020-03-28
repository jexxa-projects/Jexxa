package io.ddd.jexxa.core.factory;

import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang.Validate;

public class DrivingAdapterFactory
{
    public <T> T createByType(Class<T> instanceType, Properties properties) {
        Validate.notNull(instanceType);
        Validate.notNull(properties);


        Object[] args = new Object[1];
        args[0]= properties;

        //Apply 1. convention and try to use a constructor accepting properties
        var instance = ClassFactory.newInstanceOf(instanceType, args);

        //Apply 2. convention and try to use a factory method accepting properties
        if (instance.isEmpty())
        {
            instance = Optional.ofNullable(ClassFactory.createByFactoryMethod(instanceType, instanceType, properties));
        }

        //Apply 2. convention Try to use default constructor
        if (instance.isEmpty())
        {
            return createByType(instanceType);
        }

        return instanceType.cast(instance.orElseThrow());
    }


    public <T> T createByType(Class<T> instanceType) {
        Validate.notNull(instanceType);

        //Apply 1. convention and try to use a constructor accepting properties
        var instance = ClassFactory.newInstanceOf(instanceType);


        //Apply 2. convention and try to use a factory method accepting properties
        if (instance.isEmpty())
        {
            instance = Optional.ofNullable(ClassFactory.createByFactoryMethod(instanceType, instanceType));
        }

        return instanceType.cast(instance.orElseThrow());
    }
}
