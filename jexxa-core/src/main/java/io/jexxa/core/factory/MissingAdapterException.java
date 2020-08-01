package io.jexxa.core.factory;

import java.util.ArrayList;
import java.util.Arrays;

public class MissingAdapterException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private final String internalMessage;

    public MissingAdapterException(Class<?> port, AdapterFactory drivenAdapterFactory)
    {
        internalMessage = getInternalMessage(port, drivenAdapterFactory);
    }

    @Override
    public String getMessage()
    {
        return internalMessage;
    }


    private String getInternalMessage(Class<?> port, AdapterFactory drivenAdapterFactory)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Could not create port: ")
                .append(port.getName()).append("\n")
                .append("Missing DrivenAdapter:\n");


        var missingAdapters = new ArrayList<Class<?>>();
        Arrays.asList(port.getConstructors())
                .forEach( element ->
                        missingAdapters.addAll(drivenAdapterFactory.getMissingAdapter(Arrays.asList(element.getParameterTypes())))
                );

        missingAdapters.forEach( missingAdapter -> stringBuilder.append("    * ").append(missingAdapter.getName()).append("\n") );

        return stringBuilder.toString();
    }
}
