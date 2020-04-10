package io.ddd.jexxa.core.factory;

import static io.ddd.jexxa.utils.ThrowingConsumer.exceptionCollector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

/*+
 * This class is responsible for creating instances of ports including all required parameter.
 */
public class PortFactory
{
    private List<String> whiteListPackages = new ArrayList<>();
    private ObjectPool objectPool = new ObjectPool();
    private AdapterFactory drivenAdapterFactory;

    
    public PortFactory(AdapterFactory drivenAdapterFactory)
    {
        this.drivenAdapterFactory = drivenAdapterFactory;
    }

    public PortFactory whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        return this;
    }

    /*
     * Check if an inbound port including all required driven adapter can be created
     */
    public boolean isCreatable(Class<?> inboundPort)
    {
        return findConstructor(inboundPort).isPresent();
    }


    /***
     * Creates a new instance of given inbound port each time this method is called 
     *
     * @see #getInstanceOf(Class, Properties) 
     * @param inboundPort type of the inbound port
     * @param drivenAdapterProperties properties required to create and configure driven adapter
     * @return a new instance of inbound port 
     */
    public <T> T newInstanceOf(Class<T> inboundPort, Properties drivenAdapterProperties)
    {
        Validate.notNull(inboundPort);
        Validate.notNull(drivenAdapterProperties);

        var supportedConstructor = findConstructor(inboundPort).
                orElseThrow(() -> new MissingDrivenAdapterException(inboundPort, drivenAdapterFactory));

        var drivenAdapter = createDrivenAdapterForConstructor(supportedConstructor, drivenAdapterProperties);
        
        return ClassFactory.
                newInstanceOf(inboundPort, drivenAdapter).
                orElseThrow();
    }

    /***
     * Checks if an instance of given inbound port was already created using this methods and returns this instance.
     * Only if no instance was created so far using this method a new one is created.
     *
     * @see #newInstanceOf(Class, Properties) 
     * @param inboundPort type of the inbound port
     * @param drivenAdapterProperties properties required to create and configure driven adapter
     * @return Either a new instance of inbound port or an instance that was previously created 
     */
    public <T> T getInstanceOf(Class<T> inboundPort, Properties drivenAdapterProperties)
    {
        Validate.notNull(inboundPort);
        Validate.notNull(drivenAdapterProperties);


        var existingInstance = objectPool.getInstance(inboundPort);

        if (existingInstance.isPresent()) {
            return existingInstance.get();
        }

        var newInstance = newInstanceOf(inboundPort, drivenAdapterProperties);
        objectPool.add(newInstance);
        return newInstance;
    }

    public List<Object> getInstanceOfPorts(Class <? extends Annotation> portAnnotation, Properties drivenAdapterProperties) {
        var annotationScanner = new DependencyScanner().
                whiteListPackages(whiteListPackages);

        var scannedInboundPorts = annotationScanner.getClassesWithAnnotation(portAnnotation);

        var result = new ArrayList<>();
        var exceptionList = new ArrayList<Throwable>();

        scannedInboundPorts.
                forEach(exceptionCollector(
                        element -> result.add(getInstanceOf(element, drivenAdapterProperties)),
                        exceptionList)
                );

        exceptionList.forEach(element -> JexxaLogger.getLogger(getClass()).warn(element.getMessage()));

        return result;
    }



    /*
     * Find a constructor whose parameter can be instantiated
     */
    private Optional<Constructor<?>> findConstructor(Class<?> inboundPort)
    {
        var constructorList = Arrays.asList(inboundPort.getConstructors());

        if ( constructorList.size() > 1)
        {
            JexxaLogger.getLogger(getClass()).
                    warn("More than one constructor available for {}. => Reconsider to provide only a single constructor", inboundPort.getName());
        }

        return constructorList.
                stream().
                filter(constructor -> drivenAdapterFactory.isAvailable(Arrays.asList(constructor.getParameterTypes()))).
                findFirst();
    }


    private Object[] createDrivenAdapterForConstructor(Constructor<?> portConstructor, Properties drivenAdapterProperties)
    {
        var objectList = new ArrayList<>();

        for ( int i = 0; i < portConstructor.getParameterTypes().length; ++i )
        {
            try
            {
                objectList.add( drivenAdapterFactory.newInstanceOf(portConstructor.getParameterTypes()[i], drivenAdapterProperties) );
            }
            catch ( Exception e)
            {
                JexxaLogger.getLogger(getClass()).error("Can not create inbound port {}", portConstructor.getName());
                return new Object[0];
            }
        }

        return objectList.toArray();
    }


}
