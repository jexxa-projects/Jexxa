package io.jexxa.core.factory;

import java.io.Serial;

public class InvalidAdapterException extends RuntimeException
{
    private static final String CANNOT_CREATE_ADAPTER = "Cannot create adapter ";
    private static final String CHECK_CONVENTIONS = "For further information check the conventions of an adapter (https://jexxa-projects.github.io/Jexxa/jexxa_reference.html#_dependency_injection_di). ";

    @Serial
    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    @SuppressWarnings("unused")
    public <T> InvalidAdapterException(Class<T> adapter)
    {
        this.errorMessage = CANNOT_CREATE_ADAPTER + adapter.getSimpleName() + " -> " + CHECK_CONVENTIONS;
    }

    public <T> InvalidAdapterException(Class<T> adapter, String message)
    {
        this.errorMessage = CANNOT_CREATE_ADAPTER + adapter.getSimpleName() + "! "+ message + " -> " + CHECK_CONVENTIONS;
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