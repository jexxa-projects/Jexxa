package io.jexxa.core.convention;

import java.util.Arrays;

public final class PortConvention
{

    /**
     * Validates if given clazz matches the convention of a port.
     * If one of the conventions are not matched an exception is thrown:
     * <t>
     *     <li>
     *         Exactly one public constructor.
     *     </li>
     *     <li>
     *         Attributes of the constructor must be interfaces
     *     </li>
     * </t>
     *
     * @param clazz that should be used as a Port
     * @param <T> generic type of the port
     */
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

        var nonInterfaceAttribute =
                Arrays.stream(clazz.getConstructors()[0].getParameterTypes())
                .filter(attribute -> !attribute.isInterface())
                .findAny();

        if (nonInterfaceAttribute.isPresent())
        {
            throw new PortConventionViolation(clazz);
        }
    }


    private PortConvention()
    {
        //Private Constructor
    }

}
