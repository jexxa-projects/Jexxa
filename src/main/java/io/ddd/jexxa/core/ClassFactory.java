package io.ddd.jexxa.core;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

public class ClassFactory
{
    public static <T> T createByConstructor(Class<T> clazz)
    {
        Validate.notNull(clazz);

        var defaultConstructor = searchDefaultConstructor(clazz);
        if (defaultConstructor.isPresent()) {
            try
            {
                return clazz.cast(defaultConstructor.get().newInstance());
            }  catch (Exception e)
            {
                JexxaLogger.getLogger(ClassFactory.class).error(e.getMessage());
                throw new IllegalArgumentException("Could not create class " + clazz.getSimpleName());
            }
        }

        return null;
    }


    public static <T> T createByConstructor(Class<T> clazz, Properties properties)
    {
        Validate.notNull(clazz);
        Validate.notNull(properties);

        var propertyConstructor = searchPropertyConstructor(clazz);

        if (propertyConstructor.isPresent()) {
            try
            {
                return clazz.cast(propertyConstructor.get().newInstance(properties));
            } catch ( Exception e) {
                JexxaLogger.getLogger(ClassFactory.class).error(e.getMessage());
                throw new IllegalArgumentException("Could not create class " + clazz.getSimpleName());
            }
        }

        return null;
    }


    @SuppressWarnings("squid:S1452")
    private static Optional<Constructor<?>> searchPropertyConstructor(Class<?> clazz)
    {
        //Lookup constructor with properties
        return  Arrays.stream(clazz.getConstructors()).
                filter( element -> element.getParameterTypes().length == 1).
                filter( element -> element.getParameterTypes()[0] == Properties.class).
                findFirst();
    }

    @SuppressWarnings("squid:S1452")
    private static Optional<Constructor<?>> searchDefaultConstructor(Class<?> clazz)
    {
        //Lookup constructor with properties
        return Arrays.stream(clazz.getConstructors()).
                filter( element -> element.getParameterTypes().length == 0).
                findFirst();
    }

    private ClassFactory()
    {

    }
}
