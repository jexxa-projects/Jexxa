package io.ddd.jexxa.core;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

/*
 * Rules for creating a driving adapter:
 * 1. Public Default constructor available
 * 2. Public constructor with Properties as attribute
 * 3. Public static method with return type if the requested interface
 * 4. Public static method with return type if the requested interface and Properties as argument
 */
public class DrivenAdapterFactory
{
    private List<String> whiteListPackages = new ArrayList<>();

    DrivenAdapterFactory whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        return this;
    }

    public <T> T create(Class<T> interfaceType) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType);

        //Apply 1. convention and try to use default constructor
        var instance = interfaceType.cast(ClassFactory.createByConstructor(implementation));


        //Apply 2. convention and try to use a factory method 
        if (instance == null) {
            instance = interfaceType.cast(ClassFactory.createByFactoryMethod(implementation, interfaceType));
        }
        
        Validate.notNull(instance, "No suitable constructor found to create " + interfaceType.getName());
        
        return instance;
    }

    public <T> T create(Class<T> interfaceType, Properties properties) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType);

        //Apply 1. convention and try to use a constructor accepting properties 
        T instance = interfaceType.cast(ClassFactory.createByConstructor(implementation, properties));

        //Apply 2. convention and try to use a factory method accepting properties
        if (instance == null) {
            instance = interfaceType.cast(ClassFactory.createByFactoryMethod(implementation, interfaceType, properties));
        }

        Validate.notNull(instance, "No suitable constructor found to create " + interfaceType.getName());

        return instance;
    }

    public boolean adaptersAvailable(Class<?> service)
    {
        var constructorList = Arrays.asList(service.getConstructors());

        if ( constructorList.size() > 1)
        {
            JexxaLogger.getLogger(getClass()).warn("More than one constructor available. => Reconsider to provide only a single constructor");
        }

        var dependencyScanner = new DependencyScanner().
                whiteListPackages(whiteListPackages);

        return constructorList.stream().
                anyMatch(constructor -> validateAdaptersAvailable(Arrays.asList(constructor.getParameterTypes()), dependencyScanner) );
    }

    private boolean validateAdaptersAvailable(List<Class <?> > adapterList, DependencyScanner dependencyScanner)
    {
        return adapterList.
                stream().
                noneMatch(adapter -> getImplementationOf(adapter, dependencyScanner) == null);
    }


    private <T> Class<?> getImplementationOf(Class<T> interfaceType) {
        var dependencyScanner = new DependencyScanner().whiteListPackages(whiteListPackages);
        
        return getImplementationOf(interfaceType, dependencyScanner);
    }


    private <T> Class<?> getImplementationOf(Class<T> interfaceType, DependencyScanner dependencyScanner) {
        var results = dependencyScanner.getClassesImplementing(interfaceType);
        Validate.notNull(results);
        Validate.notEmpty(results, "No implementation of " + interfaceType.getName() + " available");
        Validate.isTrue( results.size() == 1, "Multiple implementation of " + interfaceType.getName() + " available");

        return results.get(0);
    }

}
