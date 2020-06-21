package io.jexxa.core.convention;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;

public final class PortConvention
{

    public static <T> void validate(Class<T> clazz)
    {
        if ( clazz.getConstructors().length == 0)
        {
            throw new PortConventionViolation("No public constructor available for Port : " + clazz.getName());
        }

        if ( clazz.getConstructors().length > 1)
        {
            throw new PortConventionViolation("More than one public constructor available for Port : " + clazz.getName());
        }

        var result = Arrays
                .stream(clazz.getConstructors()[0].getParameterTypes())
                .filter( attribute -> !attribute.isInterface())
                .collect(toList());

        if (!result.isEmpty())
        {
            throw new PortConventionViolation("Public constructor of Port " + clazz.getName() + " has non-interfaces as arguments.");
        }
    }

    private PortConvention()
    {
        //Private Constructor  
    }

}
