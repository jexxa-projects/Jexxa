package io.jexxa.core.convention;

public class PortConventionViolation extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    PortConventionViolation(String message)
    {
        super(message);
    }
}
