package io.ddd.jexxa.core.factory;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;

/***
 * Creates a driving or driving adapter which fulfill following requirements:
 * 1. Public Default constructor available
 * 2. Public constructor with one Properties as attribute is available
 * 3. Public static factory method with return type if the requested interface
 * 4. Public static factory method with return type if the requested interface and Properties as argument
 */
public class AdapterFactory
{
    private ObjectPool objectPool = new ObjectPool();
    private DependencyScanner dependencyScanner = new DependencyScanner();

    public AdapterFactory whiteListPackage(String packageName)
    {
        dependencyScanner.whiteListPackage(packageName);
        return this;
    }

    public <T> T newInstanceOf(Class<T> interfaceType) {
        Validate.notNull(interfaceType);

        Class<?> factory = getImplementationOf(interfaceType).
                orElseThrow(() -> new RuntimeException("No implementation found for interface " + interfaceType.getName()));

        //Apply 1. convention and try to use default constructor
        var instance = ClassFactory.newInstanceOf(factory);

        //Apply 2. convention and try to use a factory method
        if (instance.isEmpty())
        {
            instance = ClassFactory.newInstanceOf(interfaceType, factory);
        }

        return interfaceType.cast(instance.orElseThrow());
    }

    public <T> T newInstanceOf(Class<T> interfaceType, Properties properties) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType).
                orElseThrow(() -> new RuntimeException("No implementation found for interface " + interfaceType.getName()));

        //Apply 1. convention and try to use a constructor accepting properties
        var instance = ClassFactory.newInstanceOf(implementation, new Object[]{properties});

        //Apply 2. convention and try to use a factory method accepting properties
        if (instance.isEmpty())
        {
            instance = ClassFactory.newInstanceOf(interfaceType, implementation, new Object[]{properties});
        }

        //Try to create without properties 
        if (instance.isEmpty())
        {
            return newInstanceOf(interfaceType);
        }
        
        return interfaceType.cast(instance.orElseThrow());
    }


    public <T> T getInstanceOf(Class<T> interfaceType)
    {
        var existingInstance = objectPool.getInstance(interfaceType);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        T newInstance = newInstanceOf(interfaceType);
        objectPool.add(newInstance);
        return newInstance;
    }

    public <T> T getInstanceOf(Class<T> interfaceType, Properties properties)
    {
        var existingInstance = objectPool.getInstance(interfaceType);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        T newInstance = newInstanceOf(interfaceType, properties);
        objectPool.add(newInstance);
        return newInstance;
    }


    List<Class<?>> getMissingAdapter(List<Class <?> > adapterList)
    {
        return adapterList.
                stream().
                filter(adapter -> getImplementationOf(adapter, dependencyScanner).isEmpty()).
                collect(Collectors.toList());
    }

    boolean isAvailable(List<Class <?> > adapterList)
    {
        return adapterList.
                stream().
                noneMatch(adapter -> getImplementationOf(adapter, dependencyScanner).isEmpty());
    }


    /***
     * This method returns the parameter required by constructor
     * TODO refactor this method
     */
    public <T> Class<?> requiredPort(Class<T> portWrapper)
    {
        return Arrays.stream(portWrapper.getConstructors()).
                filter(constructor -> constructor.getParameterCount() == 1 ).
                findFirst().
                <Class<?>>map(constructor -> constructor.getParameterTypes()[0]).
                orElse(null);
    }
    /***
     * Returns a class which implements given interface type. In case given type is not an interface the given type is returned
     **
     * @param interfaceType class of the interface for which an implementation is required
     * @param <T> Type information of the given interface
     * @return 1. Given interface type if interfaceType is not an interface. 2. An implementation of the interface if available 
     */
    private <T> Optional<Class<?>> getImplementationOf(Class<T> interfaceType) {
        if (!interfaceType.isInterface())
        {
            return Optional.of(interfaceType);
        }

        var results = dependencyScanner.getClassesImplementing(interfaceType);

        Validate.notNull(results);
        Validate.notEmpty(results, "No implementation of " + interfaceType.getName() + " available");
        Validate.isTrue( results.size() == 1, "Multiple implementation of " + interfaceType.getName() + " available");

        return Optional.of(results.get(0));
    }


    private <T> Optional<Class<?>> getImplementationOf(Class<T> interfaceType, DependencyScanner dependencyScanner) {
        var results = dependencyScanner.getClassesImplementing(interfaceType);

        if (results == null || results.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(results.get(0));
    }

}
