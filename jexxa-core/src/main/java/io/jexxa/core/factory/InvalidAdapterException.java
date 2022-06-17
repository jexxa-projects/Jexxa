package io.jexxa.core.factory;

import java.io.Serial;

public class InvalidAdapterException extends RuntimeException
{
    private static final String CANNOT_CREATE_ADAPTER = "Cannot create adapter ";
    private static final String CHECK_CONVENTIONS = "If messages from adapter do not help -> Please check if it fulfills the conventions of an adapter. ";

    @Serial
    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public <T> InvalidAdapterException(Class<T> adapter)
    {
        this.errorMessage = CANNOT_CREATE_ADAPTER + adapter.getSimpleName() + " -> " + CHECK_CONVENTIONS;
    }

    public <T> InvalidAdapterException(Class<T> adapter, Throwable exception)
    {
        super(exception);

        Throwable rootCause = exception;

        while (rootCause.getCause() != null && !rootCause.getCause().equals(rootCause))
        {
            rootCause = rootCause.getCause();
        }

        errorMessage = CANNOT_CREATE_ADAPTER + adapter.getSimpleName() + " because a(n) " + rootCause.getClass().getSimpleName() + " occurred.";
    }

    @Override
    public String getMessage()
    {
        return errorMessage;
    }
}