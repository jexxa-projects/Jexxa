package io.jexxa.adapterapi.invocation;

import java.io.Serial;

/**
 * InvocationTargetRuntimeException is an unchecked exception that wraps an exception thrown by an
 * invoked method or constructor. This exception is preferred over InvocationTargetException in case
 * of functional interfaces.
 */
public class InvocationTargetRuntimeException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 4085088731926701167L;

    private final Throwable target;

    public InvocationTargetRuntimeException(Throwable target)
    {
        this.target = target;
    }

    public Throwable getTargetException()
    {
        return target;
    }
}
