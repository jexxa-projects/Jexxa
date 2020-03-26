package io.ddd.jexxa.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.ddd.jexxa.utils.JexxaLogger;

public class ApplicationCoreFactory
{
    private List<String> whiteListPackages = new ArrayList<>();
    AdapterFactory drivenAdapterFactory;


    ApplicationCoreFactory(AdapterFactory drivenAdapterFactory)
    {
        this.drivenAdapterFactory = drivenAdapterFactory;
    }

    ApplicationCoreFactory whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        return this;
    }

    /*
     * Check if all DrivenAdapter are available for for a given port
     */
    public boolean isAvailable(Class<?> inboundPort)
    {
        var constructorList = Arrays.asList(inboundPort.getConstructors());

        if ( constructorList.size() > 1)
        {
            JexxaLogger.getLogger(getClass()).warn("More than one constructor available. => Reconsider to provide only a single constructor");
        }

        return constructorList.stream().
                anyMatch(constructor -> drivenAdapterFactory.validateAdaptersAvailable(Arrays.asList(constructor.getParameterTypes())));
    }



}
