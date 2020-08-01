package io.jexxa.core.factory;


import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.Validate;

/**
 * Creates a driving or driving adapter which fulfills one of the following requirements:
 * <ol>
 *   <li>Public Default constructor available</li>
 *   <li>Public constructor with one Properties as attribute is available</li>
 *   <li>Public static factory method with return type of the requested interface</li>
 *   <li>Public static factory method with return type of the requested interface and Properties as argument</li>
 *   <li>If an adapter is created by its interface only a single implementation must be available. This implementation must fulfill one of the above constraints </li>
 * </ol>
 */
public class AdapterFactory
{
    private final ObjectPool objectPool = new ObjectPool();
    private final DependencyScanner dependencyScanner = new DependencyScanner();

    public AdapterFactory whiteListPackage(String packageName)
    {
        dependencyScanner.whiteListPackage(packageName);
        return this;
    }

    public <T> T newInstanceOf(Class<T> interfaceType) {
        Validate.notNull(interfaceType);

        Class<?> factory = getImplementationOf(interfaceType).
                orElseThrow(() -> new IllegalArgumentException("No implementation found for interface " + interfaceType.getName()));

        try
        {
            //Apply 1. convention and try to use default constructor
            var instance = ClassFactory.newInstanceOf(factory);

            //Apply 2. convention and try to use a factory method
            if (instance.isEmpty())
            {
                instance = ClassFactory.newInstanceOf(interfaceType, factory);
            }
            return interfaceType.cast(instance.orElseThrow());
        }
        catch (ReflectiveOperationException e)
        {
            throw new InvalidAdapterConfigurationException(interfaceType, e);
        }

    }

    public <T> T newInstanceOf(Class<T> interfaceType, Properties properties) {
        Validate.notNull(interfaceType);

        Class<?> implementation = getImplementationOf(interfaceType).
                orElseThrow(() -> new IllegalArgumentException("No implementation found for interface " + interfaceType.getName()));

        try
        {
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
        catch (ReflectiveOperationException e)
        {
            throw new InvalidAdapterConfigurationException(interfaceType, e);
        }
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
        return adapterList.stream()
                .filter(adapter -> getImplementationOf(adapter).isEmpty())
                .collect(toList());
    }

    boolean isAvailable(List<Class <?> > adapterList)
    {
        return adapterList.stream()
                .noneMatch(adapter -> getImplementationOf(adapter).isEmpty());
    }


    /**
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

        var implementationList = dependencyScanner.getClassesImplementing(interfaceType);

        Validate.notNull(implementationList);
        if (implementationList.size() > 1) // If more than one implementation is available our convention is violated
        {
            throw new AmbiguousAdapterException(interfaceType, implementationList);
        }

        if ( implementationList.isEmpty() )
        {
            return Optional.empty();
        }

        return Optional.of(implementationList.get(0));
    }

    static class InvalidAdapterConfigurationException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        private final String errorMessage;

        public <T> InvalidAdapterConfigurationException(Class<T> adapter, Exception exception)
        {
            super(exception);
            if (exception.getCause() == null )
            {
                errorMessage = "Cannot create adapter " + adapter.getName() + "\n";
            }
            else
            {
                errorMessage = "Cannot create adapter " + adapter.getName() + "\n" + "Error message from adapter : " + exception.getCause().getMessage();
            }
        }

        @Override
        public String getMessage()
        {
            return errorMessage;
        }
    }
}
