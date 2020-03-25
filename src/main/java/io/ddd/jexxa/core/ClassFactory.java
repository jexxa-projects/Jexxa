package io.ddd.jexxa.core;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

public class ClassFactory
{
    private Properties properties;

    public ClassFactory(Properties properties)
    {
        this.properties = properties;
    }

    public <T> T createByConstructor(Class<T> clazz)
    {
        var propertyConstructor = searchPropertyConstructor(clazz);

        if (propertyConstructor.isPresent()) {
            try
            {
              return clazz.cast(propertyConstructor.get().newInstance(properties));
            } catch ( Exception e) {
                System.err.println(e.getMessage());
            }
        }


        var defaultConstructor = searchDefaultConstructor(clazz);
        if (defaultConstructor.isPresent()) {
            try
            {
                return clazz.cast(defaultConstructor.get().newInstance());
            }  catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }

        throw new IllegalArgumentException("Could not create class " + clazz.getSimpleName());
    }
    
    @SuppressWarnings("squid:S1452")
    private Optional<Constructor<?>> searchPropertyConstructor(Class<?> clazz)
    {
        //Lookup constructor with properties
        return  Arrays.stream(clazz.getConstructors()).
                filter( element -> element.getParameterTypes().length == 1).
                filter( element -> element.getParameterTypes()[0] == Properties.class).
                findFirst();
    }

    @SuppressWarnings("squid:S1452")
    private Optional<Constructor<?>> searchDefaultConstructor(Class<?> clazz)
    {
        //Lookup constructor with properties
        return Arrays.stream(clazz.getConstructors()).
                filter( element -> element.getParameterTypes().length == 0).
                findFirst();
    }

}
