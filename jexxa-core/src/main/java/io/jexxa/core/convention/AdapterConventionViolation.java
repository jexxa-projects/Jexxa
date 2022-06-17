package io.jexxa.core.convention;

import java.io.Serial;

public class AdapterConventionViolation extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 1L;

    AdapterConventionViolation(Class<?> clazz)
    {
        super("No suitable constructor available for adapter : " + clazz.getName());
    }
}