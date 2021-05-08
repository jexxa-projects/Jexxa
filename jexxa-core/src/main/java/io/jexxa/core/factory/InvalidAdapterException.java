package io.jexxa.core.factory;

public class InvalidAdapterException extends RuntimeException
{
    private static final String CANNOT_CREATE_ADAPTER = "Cannot create adapter ";
    private static final String CHECK_CONVENTIONS = "Please check if it fulfills the conventions of an adapter";

    private static final long serialVersionUID = 1L;

    private final String errorMessage;

    public <T> InvalidAdapterException(Class<T> adapter)
    {
        this.errorMessage = CANNOT_CREATE_ADAPTER + adapter.getName() + "\n" + CHECK_CONVENTIONS;
    }

    public <T> InvalidAdapterException(Class<T> adapter, Throwable exception)
    {
        super(exception);
        if (exception.getCause() == null )
        {
            errorMessage = CANNOT_CREATE_ADAPTER + adapter.getName() + "\n" +
                    CHECK_CONVENTIONS;
        }
        else
        {
            errorMessage = CANNOT_CREATE_ADAPTER + adapter.getName() + "\n" +
                    CHECK_CONVENTIONS + "\n"  +
                    "Error message from adapter : " + exception.getCause().getMessage();
        }
    }

    @Override
    public String getMessage()
    {
        return errorMessage;
    }
}