package io.jexxa.adapterapi.invocation;

public class InvocationTargetRuntimeException extends RuntimeException
{
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
