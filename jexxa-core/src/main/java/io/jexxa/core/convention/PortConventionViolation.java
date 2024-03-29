package io.jexxa.core.convention;

import java.io.Serial;

public class PortConventionViolation extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    PortConventionViolation(String message)
    {
        super(message);
    }
    PortConventionViolation(Class<?> clazz)
    {
        super("Public constructor of " + clazz.getName() + " is invalid. \n" +
                "In case of an inbound port, all attributes of the constructor must be java-interfaces.\n" +
                "In case of a port adapter, the constructor must (1) take a single attribute representing an inbound port and optionally a Properties object and (2) the package must be part of the infrastructure of Jexxa.\n" +
                "For more information please refer to https://jexxa-projects.github.io/Jexxa/jexxa_architecture.html#_dependency_injection_di .");
    }
}
