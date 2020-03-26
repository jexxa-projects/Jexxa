package io.ddd.jexxa.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

public class ClassFactory
{
    public static class ClassFactoryException extends RuntimeException
    {
        public ClassFactoryException(Class<?> clazz)
        {
            super("Could not create class" + clazz.getName());
        }
    }
    
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
                throw new ClassFactoryException(clazz);
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
                throw new ClassFactoryException(clazz);
            }
        }

        return null;
    }

    public static <T> T createByFactoryMethod(Class<?> implementation, Class<T> interfaceType)
    {
        Validate.notNull(implementation);

        var method = searchDefaultFactoryMethod(implementation, interfaceType);
        if (method.isPresent()) {
            try
            {
                return interfaceType.cast(method.get().invoke(null, (Object[])null));
            }  catch (Exception e)
            {
                JexxaLogger.getLogger(ClassFactory.class).error(e.getMessage());
                throw new ClassFactoryException(interfaceType);
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

    @SuppressWarnings("squid:S1452")
    private static <T> Optional<Method> searchDefaultFactoryMethod(Class<?> implementation, Class<T> interfaceType)
    {
        //Lookup factory method with no attributes and return type clazz
        return Arrays.stream(implementation.getMethods()).
                filter( element -> Modifier.isStatic(element.getModifiers())).
                filter( element -> element.getParameterTypes().length == 0).
                filter( element -> element.getReturnType().equals(interfaceType)).
                findFirst();
    }

    private ClassFactory()
    {

    }
}
