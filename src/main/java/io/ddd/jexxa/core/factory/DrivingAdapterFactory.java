package io.ddd.jexxa.core.factory;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang.Validate;

public class DrivingAdapterFactory
{
    private ObjectPool objectPool = new ObjectPool();
    
    public <T> T newInstanceOf(Class<T> instanceType, Properties properties) {
        Validate.notNull(instanceType);
        Validate.notNull(properties);

        var instance = ClassFactory.newInstanceOf(instanceType, new Object[] {properties});

        if (instance.isEmpty())
        {
            instance = ClassFactory.newInstanceOfInterface(instanceType, instanceType, new Object[] {properties});
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
            instance = ClassFactory.newInstanceOfInterface(instanceType, instanceType);
        }

        return instanceType.cast(instance.orElseThrow());
    }

    public <T> T newInstanceOfWrapper(Class<T> instanceType, Object port) {
        Validate.notNull(instanceType);

        var instance = ClassFactory.newInstanceOf(instanceType, new Object[]{port});

        return instanceType.cast(instance.orElseThrow());
    }

    public <T> Class<?> requiredPort(Class<T> portWrapper)
    {
        return Arrays.stream(portWrapper.getConstructors()).
                filter(constructor -> constructor.getParameterCount() == 1 ).
                findFirst().
                <Class<?>>map(constructor -> constructor.getParameterTypes()[0]).
                orElse(null);
    }



    public <T> T getInstanceOf(Class<T> instanceType, Properties properties) {
        var existingInstance = objectPool.getInstance(instanceType);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        T newInstance = newInstanceOf(instanceType, properties);
        objectPool.add(newInstance);
        return newInstance;
    }

    public <T> T getInstanceOf(Class<T> instanceType) {
        var existingInstance = objectPool.getInstance(instanceType);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        T newInstance = newInstanceOf(instanceType);
        objectPool.add(newInstance);
        return newInstance;
    }

}
