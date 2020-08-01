package io.jexxa.core.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.ThrowingConsumer;
import org.apache.commons.lang3.Validate;

/**
 * This class is responsible for creating instances of ports or a wrapper for a port including all required parameter.
 * <p>
 * Conventions for ports
 * <ol>
 *    <li>A port has a single public constructor with parameters of all required driven adapters. <br>
 *        <strong>Note: The need for multiple constructors with different parameters is strong indication of violation of SRP</strong></li>
 *
 *  <li> All parameters of the single constructor of a port must be interfaces.
 *      <strong>Note: In case you have to pass concrete instance into the application is a strong indication that separation of technology
 *           stacks from application core is violated.</strong></li>
 *
 *  <li>A wrapper for a port has a single public constructor with a single parameter which is a port. In order to create the port properly, the above
 *      conventions must be fulfilled.</li>
 * </ol>
 */
public class PortFactory
{
    private final List<String> whiteListPackages = new ArrayList<>();
    private final ObjectPool objectPool = new ObjectPool();
    private final AdapterFactory adapterFactory;
    private CreationPolicy drivenAdapterPolicy = CreationPolicy.REUSE;

    public enum CreationPolicy{REUSE,NEW_INSTANCE}
    
    public PortFactory(AdapterFactory adapterFactory)
    {
        this.adapterFactory = adapterFactory;
    }

    public PortFactory whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        return this;
    }

    @SuppressWarnings("unused")
    public void setDrivenAdapterPolicy(CreationPolicy creationPolicy)
    {
        this.drivenAdapterPolicy = creationPolicy;
    }

    /**
     * Check if an inbound port including all required driven adapter can be created
     *
     * @param inboundPort type of the port to check if it is in general available
     * @return true in case a constructor fulfilling conventions is available, otherwise false 
     */
    public boolean isAvailable(Class<?> inboundPort)
    {
        return findConstructor(inboundPort).isPresent();
    }


    /**
     * Checks if an instance of given inbound port was already created using this methods and returns this instance.
     * Only if no instance was created so far using this method a new one is created.
     *
     * @see #newInstanceOf(Class, Properties) 
     * @param inboundPort type of the inbound port
     * @param adapterProperties properties required to create and configure driven adapter
     * @param <T> Type of the object that should be created
     * @return Either a new instance of inbound port or an instance that was previously created 
     */
    public <T> T getInstanceOf(Class<T> inboundPort, Properties adapterProperties)
    {
        Validate.notNull(inboundPort);
        Validate.notNull(adapterProperties);


        var existingInstance = objectPool.getInstance(inboundPort);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        var newInstance = newInstanceOf(inboundPort, adapterProperties);
        objectPool.add(newInstance);
        return newInstance;
    }

    public List<Object> getInstanceOfPorts(Class <? extends Annotation> portAnnotation, Properties adapterProperties) {
        var annotationScanner = new DependencyScanner().
                whiteListPackages(whiteListPackages);

        var scannedInboundPorts = annotationScanner.getClassesWithAnnotation(portAnnotation);

        var result = new ArrayList<>();
        var exceptionList = new ArrayList<Throwable>();

        scannedInboundPorts.
                forEach(ThrowingConsumer.exceptionCollector(
                        element -> result.add(getInstanceOf(element, adapterProperties)),
                        exceptionList)
                );

        exceptionList.forEach(element -> JexxaLogger.getLogger(getClass()).warn(element.getMessage()));

        return result;
    }

    /**
     * This method creates a so called port-adapter including the managed port. A port-adapter typically
     * performs a specific mapping from a generic driving adapter, such as JMS, to a specific type of port.
     * <p>
     *
     * Note: The port is created using {@link #getInstanceOf(Class, Properties)}*
     *
     * @param portAdapter type of the port-adapter. Note: This method expects that the port-adapter has a single constructor
     *                    which takes exactly one port as argument.
     * @param properties properties required to initialize the driven adapter of the port
     * @param <T> type of the port-adapter
     * @return the created port-adapter including its port 
     */
    public <T> T getPortAdapterOf(Class<T> portAdapter, Properties properties)
    {
        var portInstance = getInstanceOf(getPort(portAdapter), properties);

        try {
            return ClassFactory.newInstanceOf(portAdapter, new Object[]{portInstance})
                .orElseThrow(() -> new MissingAdapterException(portInstance.getClass(), adapterFactory));
        }
            catch (ReflectiveOperationException e)
        {
            throw new InvalidPortConfigurationException(portAdapter, e);
        }
}

    /**
     * Creates a new instance of given inbound port each time this method is called
     *
     * @see #getInstanceOf(Class, Properties)
     * @param inboundPort type of the inbound port
     * @param adapterProperties properties required to create and configure driven adapter
     * @param <T> Type of the object that should be created
     * @return a new instance of inbound port
     */
    <T> T newInstanceOf(Class<T> inboundPort, Properties adapterProperties)
    {
        Validate.notNull(inboundPort);
        Validate.notNull(adapterProperties);

        var portConstructor = findConstructor(inboundPort)
                .orElseThrow(() -> new MissingAdapterException(inboundPort, adapterFactory));

        var dependencies = createDependencies(portConstructor, adapterProperties);
        try
        {
            return ClassFactory.newInstanceOf(inboundPort, dependencies)
                    .orElseThrow();
        }
        catch (ReflectiveOperationException e)
        {
            throw new InvalidPortConfigurationException(inboundPort, e);
        }
    }


    /**
     * Find a constructor whose parameter can be instantiated an adapterFactory 
     *
     * @param inboundPort class information of the inbound port
     * @return a constructor that can be used to create a port or an empty Optional 
     */
    private Optional<Constructor<?>> findConstructor(Class<?> inboundPort)
    {
        var constructorList = Arrays.asList(inboundPort.getConstructors());
        
        return constructorList.stream()
                .filter(constructor -> adapterFactory.isAvailable(Arrays.asList(constructor.getParameterTypes())))
                .findFirst();
    }


    private Object[] createDependencies(Constructor<?> portConstructor, Properties adapterProperties)
    {
        var objectList = new ArrayList<>();

        for ( int i = 0; i < portConstructor.getParameterTypes().length; ++i )
        {
            //Depending on creation policy we create a new instance or try to reuse existing instance
            if ( drivenAdapterPolicy == CreationPolicy.NEW_INSTANCE) {
                objectList.add( adapterFactory.newInstanceOf(portConstructor.getParameterTypes()[i], adapterProperties) );
            }
            else
            {
                objectList.add( adapterFactory.getInstanceOf(portConstructor.getParameterTypes()[i], adapterProperties) );
            }
        }

        return objectList.toArray();
    }

    /**
     * Returns the port that is required by given port-adapter which is by convention the first and only parameter of th constructor
     *
     * @param portAdapter Class information of the portAdapter
     * @param <T> type of the portAdapter
     * @return Class information of the port that is used by this port-adapter which is by convention the first and only parameter of th constructor
     */
    private <T> Class<?> getPort(Class<T> portAdapter)
    {
        return Arrays.stream(portAdapter.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == 1)
                .filter(constructor -> !constructor.getParameterTypes()[0].isInterface())
                .findFirst()
                .<Class<?>>map(constructor -> constructor.getParameterTypes()[0])
                .orElseThrow(() -> new RuntimeException("PortWrapper " + portAdapter.getSimpleName() + " requires unknown port"));
    }

    static class InvalidPortConfigurationException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        private final String errorMessage;

        public <T> InvalidPortConfigurationException(Class<T> port, Exception exception)
        {
            super(exception);
            if (exception.getCause() == null )
            {
                errorMessage = "Cannot create adapter " + port.getName() + "\n";
            }
            else
            {
                errorMessage = "Cannot create port " + port.getName() + "\n" + "Error message from adapter : " + exception.getCause().getMessage();
            }
        }

        @Override
        public String getMessage()
        {
            return errorMessage;
        }
    }
}
