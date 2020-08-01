package io.jexxa.core.factory;

import java.util.List;

public class AmbiguousAdapterException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final String internalMessage;

    public <T> AmbiguousAdapterException(Class<T> clazz, List<Class<?>> implementationList)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("AmbiguousAdapterException: Outbound port ")
                .append(clazz.getName())
                .append(" is implemented by multiple adapters : \n");

        implementationList.forEach( implementation -> stringBuilder.append("   * ")
                .append(implementation.getName())
                .append("\n")
        );

        internalMessage = stringBuilder.toString();
    }

    @Override
    public String getMessage()
    {
        return internalMessage;
    }

}
