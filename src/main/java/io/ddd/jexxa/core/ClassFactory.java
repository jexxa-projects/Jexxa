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
              return (T) propertyConstructor.get().newInstance(properties);
            } catch ( Exception e) {
                System.err.println(e.getMessage());
            }
        }


        var defaultConstructor = searchDefaultConstructor(clazz);
        if (defaultConstructor.isPresent()) {
            try
            {
                return (T) defaultConstructor.get().newInstance();
            }  catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
        }

        return null;
    }
    
    @SuppressWarnings("squid:S1452")
    Optional<Constructor<?>> searchPropertyConstructor(Class<?> clazz)
    {
        //Lookup constructor with properties
        return  Arrays.stream(clazz.getConstructors()).
                filter( element -> element.getTypeParameters().length == 1 && element.getParameterTypes()[0] == Properties.class).
                //filter( element -> element.getParameterTypes()[0] == Properties.class).
                findFirst();
    }

    @SuppressWarnings("squid:S1452")
    Optional<Constructor<?>> searchDefaultConstructor(Class<?> clazz)
    {
        //Lookup constructor with properties
        return Arrays.stream(clazz.getConstructors()).
                filter( element -> element.getTypeParameters().length == 0).
                findFirst();
    }

}
