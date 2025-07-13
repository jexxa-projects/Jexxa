package io.jexxa.core.factory;


import io.jexxa.common.facade.factory.ClassFactory;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Creates all kinds of adapters (driving and driven) which fulfill one of the following conventions:
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

    public AdapterFactory acceptPackage(String packageName)
    {
        dependencyScanner.acceptPackage(packageName);
        return this;
    }

    public List<String> getAcceptPackages()
    {
        return dependencyScanner.getAcceptPackages();
    }

    public <T> T newInstanceOf(Class<T> adapterInterface) {
        Objects.requireNonNull(adapterInterface);

        Class<?> adapterImpl = getImplementationOf(adapterInterface).
                orElseThrow(() -> new IllegalArgumentException("No implementation found for interface " + adapterInterface.getName()));

        try
        {
            // Apply the 1st convention and try to use default constructor
            var adapterInstance = ClassFactory.newInstanceOf(adapterImpl);

            // Apply the 2nd convention and try to use a factory method
            if (adapterInstance.isEmpty())
            {
                adapterInstance = ClassFactory.newInstanceOf(adapterInterface, adapterImpl);
            }
            return adapterInterface.cast(adapterInstance.orElseThrow(() -> new InvalidAdapterException(adapterImpl, "No suitable public constructor or public factory method available!")));
        }
        catch (ReflectiveOperationException e)
        {
            throw new InvalidAdapterException(adapterInterface, e);
        }

    }

    public <T> T newInstanceOf(Class<T> adapterInterface, Properties properties) {
        Objects.requireNonNull(adapterInterface);

        Class<?> adapterImpl = getImplementationOf(adapterInterface).
                orElseThrow(() -> new IllegalArgumentException("No implementation found for interface " + adapterInterface.getName()));

        try
        {
            // Apply the 1st convention and try to use a constructor accepting properties
            var adapterInstance = ClassFactory.newInstanceOf(adapterImpl, new Object[]{properties});

            // Apply the 2nd convention and try to use a factory method accepting properties
            if (adapterInstance.isEmpty())
            {
                adapterInstance = ClassFactory.newInstanceOf(adapterInterface, adapterImpl, new Object[]{properties});
            }

            //Try to create without properties
            if (adapterInstance.isEmpty())
            {
                return newInstanceOf(adapterInterface);
            }

            return adapterInterface.cast(adapterInstance.orElseThrow(() -> new InvalidAdapterException(adapterImpl, "No suitable public constructor or public factory method available!")));

        }
        catch (ReflectiveOperationException e)
        {
            throw new InvalidAdapterException(adapterInterface, e);
        }
    }


    public <T> T getInstanceOf(Class<T> adapterInterface, Properties properties)
    {
        var existingInstance = objectPool.getInstance(adapterInterface);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        var newInstance = newInstanceOf(adapterInterface, properties);
        objectPool.add(newInstance);
        return newInstance;
    }


    List<Class<?>> getMissingAdapter(List<Class <?> > adapterList)
    {
        return adapterList.stream()
                .filter(adapter -> getImplementationOf(adapter).isEmpty())
                .toList();
    }

    boolean isAvailable(List<Class <?> > adapterList)
    {
        return adapterList.stream()
                .noneMatch(adapter -> getImplementationOf(adapter).isEmpty());
    }


    /**
     * Returns a class which implements a given interface type.
     * In case a given type is not an interface, the given type is returned
     *
     * @param adapterInterface class of the interface for which an implementation is required
     * @param <T> Type information of the given interface
     * @return 1. Given interface type if interfaceType is not an interface. 2. An implementation of the interface if available
     */
    private <T> Optional<Class<?>> getImplementationOf(Class<T> adapterInterface) {
        if ( !Modifier.isInterface(adapterInterface.getModifiers()) &&
             !Modifier.isAbstract(adapterInterface.getModifiers()) )
        {
            return Optional.of(adapterInterface);
        }

        var implementationList = dependencyScanner.getClassesImplementing(adapterInterface);

        if (implementationList.size() > 1) // If more than one implementation is available, our convention is violated
        {
            throw new AmbiguousAdapterException(adapterInterface, implementationList);
        }

        if ( implementationList.isEmpty() )
        {
            return Optional.empty();
        }

        return Optional.of(implementationList.get(0));
    }

    public void clear()
    {
        objectPool.clear();
    }

}
