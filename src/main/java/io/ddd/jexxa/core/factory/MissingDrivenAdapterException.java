package io.ddd.jexxa.core.factory;

import java.util.ArrayList;
import java.util.Arrays;

public class MissingDrivenAdapterException extends RuntimeException
{
    private final String internalMessage;

    public MissingDrivenAdapterException(Class<?> port, DrivenAdapterFactory drivenAdapterFactory)
    {
        internalMessage = getInternalMessage(port, drivenAdapterFactory);
    }

    @Override
    public String getMessage()
    {
        return internalMessage;
    }


    private String getInternalMessage(Class<?> port, DrivenAdapterFactory drivenAdapterFactory)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Could not create port: ").
                append(port.getName()).append("\n").
                append("Missing DrivenAdapter:\n");


        var missingAdapters = new ArrayList<Class<?>>();
        Arrays.asList(port.getConstructors()).
                forEach( element -> missingAdapters.addAll(drivenAdapterFactory.getMissingAdapter(Arrays.asList(element.getParameterTypes()))));
        missingAdapters.forEach( missingAdapter -> stringBuilder.append("    * ").append(missingAdapter.getName()).append("\n") );

        return stringBuilder.toString();
    }
}
