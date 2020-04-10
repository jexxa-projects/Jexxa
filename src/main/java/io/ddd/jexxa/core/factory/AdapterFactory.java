package io.ddd.jexxa.core.factory;


import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;

/***
 * Creates a driving or driving adapter which fulfill following requirements:
 *
 * 1. Public Default constructor available
 * 2. Public constructor with one Properties as attribute is available
 * 3. Public static factory method with return type of the requested interface
 * 4. Public static factory method with return type of the requested interface and Properties as argument
 * 5. If an adapter is created by its interface only a single implementation must be available. This implementation must fulfill above constraints  
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
                orElseThrow(() -> new IllegalArgumentException("No implementation found for interface " + interfaceType.getName()));

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
                orElseThrow(() -> new IllegalArgumentException("No implementation found for interface " + interfaceType.getName()));

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
                filter(adapter -> getImplementationOf(adapter).isEmpty()).
                collect(Collectors.toList());
    }

    boolean isAvailable(List<Class <?> > adapterList)
    {
        return adapterList.
                stream().
                noneMatch(adapter -> getImplementationOf(adapter).isEmpty());
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

        var implemenationList = dependencyScanner.getClassesImplementing(interfaceType);

        Validate.notNull(implemenationList);
        Validate.isTrue(implemenationList.size() <= 1); // If more than one implementation is available our convention is violated

        if ( implemenationList.isEmpty() )
        {
            return Optional.empty();
        }

        return Optional.of(implemenationList.get(0));
    }
}
