package io.ddd.jexxa.core.factory;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;

/*
 * One of the requirements of implementation of a DrivenAdapter must be fulfilled:
 * 1. Public Default constructor available
 * 2. Public constructor with one Properties as attribute is available
 * 3. Public static factory method with return type if the requested interface
 * 4. Public static factory method with return type if the requested interface and Properties as argument
 */
public class DrivenAdapterFactory
{
    private List<String> whiteListPackages = new ArrayList<>();
    private ObjectPool objectPool = new ObjectPool();

    public DrivenAdapterFactory whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        return this;
    }

    public <T> T newInstanceOfInterface(Class<T> interfaceType) {
        Validate.notNull(interfaceType);
        Validate.isTrue(interfaceType.isInterface(), "Given Argument is not an interface: " + interfaceType.getName());

        Class<?> implementation = getImplementationOf(interfaceType).
                orElseThrow();

        //Apply 1. convention and try to use default constructor
        var instance = ClassFactory.newInstanceOf(implementation);
        
        //Apply 2. convention and try to use a factory method 
        if (instance.isEmpty())
        {
            instance = ClassFactory.newInstanceOfInterface(implementation, interfaceType);
        }

        return interfaceType.cast(instance.orElseThrow());
    }

    public <T> T newInstanceOfInterface(Class<T> interfaceType, Properties properties) {
        Validate.notNull(interfaceType);
        Validate.isTrue(interfaceType.isInterface(), "Given Argument is not an interface: " + interfaceType.getName());

        Class<?> implementation = getImplementationOf(interfaceType).
                orElseThrow(() -> new RuntimeException("No implementation found for interface " + interfaceType.getName()));

        //Apply 1. convention and try to use a constructor accepting properties 
        var instance = ClassFactory.newInstanceOf(implementation, new Object[]{properties});

        //Apply 2. convention and try to use a factory method accepting properties
        if (instance.isEmpty())
        {
            instance = ClassFactory.newInstanceOfInterface(implementation, interfaceType, properties);
        }

        //Try to create without properties 
        if (instance.isEmpty())
        {
            return newInstanceOfInterface(interfaceType);
        }
        
        return interfaceType.cast(instance.orElseThrow());
    }


    public <T> T getInstanceOfInterface(Class<T> interfaceType) 
    {
        var existingInstance = objectPool.getInstance(interfaceType);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        T newInstance = newInstanceOfInterface(interfaceType);
        objectPool.add(newInstance);
        return newInstance;
    }

    public <T> T getInstanceOfInterface(Class<T> interfaceType, Properties properties)
    {
        var existingInstance = objectPool.getInstance(interfaceType);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        T newInstance = getInstanceOfInterface(interfaceType, properties);
        objectPool.add(newInstance);
        return newInstance;
    }


    List<Class<?>> getMissingAdapter(List<Class <?> > adapterList)
    {
        var dependencyScanner = new DependencyScanner().
                whiteListPackages(whiteListPackages);

        return adapterList.
                stream().
                filter(adapter -> getImplementationOf(adapter, dependencyScanner).isEmpty()).
                collect(Collectors.toList());
    }

    boolean isAvailable(List<Class <?> > adapterList)
    {
        var dependencyScanner = new DependencyScanner().
                whiteListPackages(whiteListPackages);
        
        return adapterList.
                stream().
                noneMatch(adapter -> getImplementationOf(adapter, dependencyScanner).isEmpty());
    }


    private <T> Optional<Class<?>> getImplementationOf(Class<T> interfaceType) {
        var dependencyScanner = new DependencyScanner().whiteListPackages(whiteListPackages);

        var results = dependencyScanner.getClassesImplementing(interfaceType);

        Validate.notNull(results);
        Validate.notEmpty(results, "No implementation of " + interfaceType.getName() + " available");
        Validate.isTrue( results.size() == 1, "Multiple implementation of " + interfaceType.getName() + " available");

        return getImplementationOf(interfaceType, dependencyScanner);
    }


    private <T> Optional<Class<?>> getImplementationOf(Class<T> interfaceType, DependencyScanner dependencyScanner) {
        var results = dependencyScanner.getClassesImplementing(interfaceType);

        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(results.get(0));
    }

}
