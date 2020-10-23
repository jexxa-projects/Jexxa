package io.jexxa.core.convention;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public final class PortConvention
{

    public static <T> void validate(Class<T> clazz, List<String> acceptedApplicationCore)
    {
        if ( !isInApplicationCore(clazz, acceptedApplicationCore) )
        {
            throw new PortConventionViolation("Port " + clazz.getName() + " is not in accepted package list of ApplicationCore");
        }

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
            throw new PortConventionViolation("Public constructor of Port " + clazz.getName() + " has non-interfaces as arguments.\n " +
                    "If this class a is port-adapter, please check that it is added to the infrastructure using JexxaMain.addToInfrastructure(). ");
        }
    }

    private static <T> boolean isInApplicationCore( Class<T> clazz, List<String> acceptedApplicationCore)
    {
        return acceptedApplicationCore
                .stream()
                .anyMatch( element -> clazz.getPackage().toString().contains( element ) );
    }

    private PortConvention()
    {
        //Private Constructor
    }

}
