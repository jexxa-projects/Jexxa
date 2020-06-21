package io.jexxa.core.convention;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;

public final class AdapterConvention
{
    public static <T> void validate(Class<T> clazz)
    {
        if (
                isDefaultConstructorAvailable(clazz)
                || isPropertiesConstructorAvailable(clazz)
                || isDefaultFactoryMethodAvailable(clazz)
                || isPropertiesFactoryMethodAvailable(clazz)
        )
        {
            return;
        }

        throw new AdapterConventionViolation("No suitable constructor available for adapter : " + clazz.getName());
    }


    public static <P> boolean isPortAdapter(Class<P> port)
    {
        return Arrays.stream(port.getConstructors())
                .filter(constructor -> constructor.getParameterTypes().length == 1)
                .anyMatch(constructor -> !constructor.getParameterTypes()[0].isInterface());
    }

    private static <T> boolean isDefaultConstructorAvailable(Class<T> clazz)
    {
        try
        {
            clazz.getConstructor();
            return true; //Default constructor available
        }
        catch (NoSuchMethodException | SecurityException ignored)
        {
            //If exception is thrown just go on to check if other type of constructor are available
        }

        return false;
    }

    private static <T> boolean isPropertiesConstructorAvailable(Class<T> clazz)
    {
        try
        {
            clazz.getConstructor(Properties.class);
            return true; //Constructor with Properties argument available
        }
        catch (NoSuchMethodException | SecurityException ignored)
        {
            //If exception is thrown just go on to check if other type of constructor are available
        }
        return false;
    }

    private static <T> boolean isDefaultFactoryMethodAvailable(Class<T> clazz)
    {
        var factoryMethods = Arrays
                .stream(clazz.getMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getReturnType().isAssignableFrom(clazz))
                .collect(toList());

        return factoryMethods.stream().anyMatch(method -> method.getParameterCount() == 0); //Factory method with no arguments available
    }

    private static <T> boolean isPropertiesFactoryMethodAvailable(Class<T> clazz)
    {
        var factoryMethods = Arrays
                .stream(clazz.getMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getReturnType().isAssignableFrom(clazz))
                .collect(toList());

        return factoryMethods.stream().anyMatch(method -> (
                method.getParameterCount() == 1 &&
                        method.getParameterTypes()[0].isAssignableFrom(Properties.class))); //Factory method with Properties argument available
    }


    private AdapterConvention()
    {
      //Private Constructor
    }
}
