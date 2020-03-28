package io.ddd.jexxa.core.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

class ClassFactory
{
    /*
     * Throw a RuntimeException in case an exception related to reflection occurs   
     */
    static class ClassFactoryException extends RuntimeException
    {
        public ClassFactoryException(Class<?> clazz)
        {
            super("Could not create class" + clazz.getName());
        }
    }
    
    static <T> Optional<T> newInstanceOf(Class<T> clazz)
    {
        Validate.notNull(clazz);

        var defaultConstructor = searchDefaultConstructor(clazz);
        if (defaultConstructor.isPresent()) {
            try
            {
                return Optional.of(clazz.cast(defaultConstructor.get().newInstance()));
            }
            catch (Exception e)
            {
                JexxaLogger.getLogger(ClassFactory.class).error(e.getMessage());
                throw new ClassFactoryException(clazz);
            }
        }

        return Optional.empty();
    }

    static <T> Optional<T> newInstanceOf(Class<T> clazz, Object[] parameter)
    {
        Validate.notNull(clazz);
        Validate.notNull(parameter);

        if (parameter.length == 0) {
            return newInstanceOf(clazz);
        }

        var parameterConstructor = searchParameterConstructor(clazz, parameter);

        if (parameterConstructor.isPresent()) {
            try
            {
                return Optional.of(clazz.cast(parameterConstructor.get().newInstance(parameter)));
            }
            catch ( Exception e)
            {
                JexxaLogger.getLogger(ClassFactory.class).error(e.getMessage());
                throw new ClassFactoryException(clazz);
            }
        }

        return Optional.empty();
    }
    

    static <T> Optional<T> newInstanceOfInterface(Class<?> implementation, Class<T> interfaceType)
    {
        Validate.notNull(implementation);

        var method = searchDefaultFactoryMethod(implementation, interfaceType);
        if (method.isPresent()) {
            try
            {
                return Optional.ofNullable(
                        interfaceType.cast(method.get().invoke(null, (Object[])null))
                );
            }
            catch (Exception e)
            {
                JexxaLogger.getLogger(ClassFactory.class).error(e.getMessage());
                throw new ClassFactoryException(interfaceType);
            }
        }

        return Optional.empty();
    }

    static <T> Optional<T> newInstanceOfInterface(Class<?> implementation, Class<T> interfaceType, Properties properties)
    {
        Validate.notNull(implementation);

        var method = searchPropertiesFactoryMethod(implementation, interfaceType);
        if (method.isPresent()) {
            try
            {
                return Optional.ofNullable(
                        interfaceType.cast(method.get().invoke(null, properties))
                );
            }  catch (Exception e)
            {
                JexxaLogger.getLogger(ClassFactory.class).error(e.getMessage());
                throw new ClassFactoryException(interfaceType);
            }
        }

        return Optional.empty();
    }


    @SuppressWarnings("squid:S1452")
    private static Optional<Constructor<?>> searchDefaultConstructor(Class<?> clazz)
    {
        return Arrays.stream(clazz.getConstructors()).
                filter( element -> element.getParameterTypes().length == 0).
                findFirst();
    }


    @SuppressWarnings("squid:S1452")
    private static <T> Optional<Constructor<?>> searchParameterConstructor(Class<T> clazz, Object[] parameter)
    {
        //Use default constructor if no parameters are set.
        if ( parameter.length == 0 ) {
            return searchDefaultConstructor(clazz);
        }
      
       var parameterTypes = Arrays.stream(parameter).map(Object::getClass).collect(Collectors. <Class<?>> toList());

       return  Arrays.stream(clazz.getConstructors()).
               filter( element -> element.getParameterTypes().length == parameter.length).
               filter (element -> isAssignableFrom(Arrays.asList(element.getParameterTypes()), parameterTypes )).
               findFirst();
    }

    private static boolean isAssignableFrom( List<Class<?>> interfaceList, List<Class<?>> implementationList )
    {
        if (interfaceList.size() != implementationList.size())
        {
            return false;
        }

        final AtomicInteger counter = new AtomicInteger(); // int is not possible because elements used in streams should be final
        return interfaceList.stream().allMatch( element -> element.isAssignableFrom(implementationList.get(counter.getAndIncrement())));
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
    

    @SuppressWarnings("squid:S1452")
    private static <T> Optional<Method> searchPropertiesFactoryMethod(Class<?> implementation, Class<T> interfaceType)
    {
        //Lookup factory method with no attributes and return type clazz
        return Arrays.stream(implementation.getMethods()).
                filter( element -> Modifier.isStatic(element.getModifiers())).
                filter( element -> element.getParameterTypes().length == 1).
                filter( element -> element.getParameterTypes()[0].equals(Properties.class)).
                filter( element -> element.getReturnType().equals(interfaceType)).
                findFirst();
    }


    private ClassFactory()
    {

    }
}
