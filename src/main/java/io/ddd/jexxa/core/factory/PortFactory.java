package io.ddd.jexxa.core.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

public class PortFactory
{
    private List<String> whiteListPackages = new ArrayList<>();
    private ObjectPool objectPool = new ObjectPool();
    private DrivenAdapterFactory drivenAdapterFactory;


    static class MissingDrivenAdapterException extends RuntimeException
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

    public PortFactory(DrivenAdapterFactory drivenAdapterFactory)
    {
        this.drivenAdapterFactory = drivenAdapterFactory;
    }

    public PortFactory whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        return this;
    }

    public Object newInstanceOf(Class<?> inboundPort, Properties drivenAdapterProperties)
    {
        Validate.notNull(inboundPort);
        Validate.notNull(drivenAdapterProperties);

        var supportedConstructor = findSupportedConstructor(inboundPort).
                orElseThrow(() -> new MissingDrivenAdapterException(inboundPort, drivenAdapterFactory));

        var drivenAdapter = createDrivenAdapterForConstructor(supportedConstructor, drivenAdapterProperties);
        
        return ClassFactory.
                newInstanceOf(inboundPort, drivenAdapter).
                orElseThrow();
    }

    public Object getInstanceOf(Class<?> inboundPort, Properties drivenAdapterProperties)
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


    /*
     * Check if all DrivenAdapter are available for for a given port
     */
    public boolean isAvailable(Class<?> inboundPort)
    {
       return findSupportedConstructor(inboundPort).isPresent();
    }

    /*
     * Check if all DrivenAdapter are available for for a given port
     */
    private Optional<Constructor<?>> findSupportedConstructor(Class<?> inboundPort)
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


    public List<Object> createPortsBy(Class <? extends Annotation> portAnnotation, Properties drivenAdapterProperties) {
        var annotationScanner = new DependencyScanner().
                whiteListPackages(whiteListPackages);

        var scannedInboundPorts = annotationScanner.getClassesWithAnnotation(portAnnotation);

        var result = new ArrayList<>();
        var exceptionList = new ArrayList<RuntimeException>();
        
        scannedInboundPorts.
                    forEach(exceptionWrapper(
                            element -> result.add(newInstanceOf(element, drivenAdapterProperties)),
                            exceptionList)
                    );

        exceptionList.forEach(element -> JexxaLogger.getLogger(getClass()).warn(element.getMessage()));

        return result;
    }

    public List<Object> getPortsBy(Class <? extends Annotation> portAnnotation, Properties drivenAdapterProperties) {
        var annotationScanner = new DependencyScanner().
                whiteListPackages(whiteListPackages);

        var scannedInboundPorts = annotationScanner.getClassesWithAnnotation(portAnnotation);

        var result = new ArrayList<>();
        var exceptionList = new ArrayList<RuntimeException>();

        scannedInboundPorts.
                forEach(exceptionWrapper(
                        element -> result.add(getInstanceOf(element, drivenAdapterProperties)),
                        exceptionList)
                );

        exceptionList.forEach(element -> JexxaLogger.getLogger(getClass()).warn(element.getMessage()));

        return result;
    }

    static <T> Consumer<T>
    exceptionWrapper(Consumer<T> consumer,  List< RuntimeException > exceptionList) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (Exception e) {
                try {
                    exceptionList.add((RuntimeException) e);
                } catch (ClassCastException ccEx) {
                    throw e;
                }
            }
        };
    }
    

    private Object[] createDrivenAdapterForConstructor(Constructor<?> portConstructor, Properties drivenAdapterProperties)
    {
        var objectList = new ArrayList<>();

        for ( int i = 0; i < portConstructor.getParameterTypes().length; ++i )
        {
            try
            {
                objectList.add( drivenAdapterFactory.newInstanceOfInterface(portConstructor.getParameterTypes()[i], drivenAdapterProperties) );
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
