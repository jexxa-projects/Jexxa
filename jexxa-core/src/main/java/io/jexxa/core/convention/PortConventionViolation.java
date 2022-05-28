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
}
