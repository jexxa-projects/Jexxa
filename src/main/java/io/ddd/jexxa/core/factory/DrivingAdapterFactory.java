package io.ddd.jexxa.core.factory;

import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang.Validate;

public class DrivingAdapterFactory
{
    public <T> T newInstanceOf(Class<T> instanceType, Properties properties) {
        Validate.notNull(instanceType);
        Validate.notNull(properties);
        
        var instance = ClassFactory.newInstanceOf(instanceType, new Object[] {properties});

        if (instance.isEmpty())
        {
            instance = Optional.ofNullable(ClassFactory.newInstanceOfInterface(instanceType, instanceType, properties));
        }

        //Try to use constructors without Properties
        if (instance.isEmpty())
        {
            return newInstanceOf(instanceType);
        }

        return instanceType.cast(instance.orElseThrow());
    }


    public <T> T newInstanceOf(Class<T> instanceType) {
        Validate.notNull(instanceType);

        var instance = ClassFactory.newInstanceOf(instanceType);


        if (instance.isEmpty())
        {
            instance = Optional.ofNullable(ClassFactory.newInstanceOfInterface(instanceType, instanceType));
        }

        return instanceType.cast(instance.orElseThrow());
    }
}
