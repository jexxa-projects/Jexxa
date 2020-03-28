package io.ddd.jexxa.core.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;

//TODO: Double check if we should split this class into two classes. One for DrivenAdapter and one for DrivingAdapter since the creation rules are different (at least at the moment) 
public class PortFactory
{
    private List<String> whiteListPackages = new ArrayList<>();
    DrivenAdapterFactory drivenAdapterFactory;


    public PortFactory(DrivenAdapterFactory drivenAdapterFactory)
    {
        this.drivenAdapterFactory = drivenAdapterFactory;
    }

    public PortFactory whiteListPackage(String packageName)
    {
        whiteListPackages.add(packageName);
        return this;
    }

    @SuppressWarnings("squid:S3655")
    public //Sonarlint does not detect validation with Validate.isTrue( supportedConstructor.isPresent() )
    Object createByType(Class<?> inboundPort, Properties drivenAdapterProperties)
    {
        Validate.notNull(inboundPort);
        Validate.notNull(drivenAdapterProperties);

        var supportedConstructor = findSupportedConstructor(inboundPort);
        Validate.isTrue( supportedConstructor.isPresent() );

        var drivenAdapter = createDrivenAdapterForConstructor(supportedConstructor.get(), drivenAdapterProperties);
        
        return ClassFactory.
                newInstanceOf(inboundPort, drivenAdapter).
                orElseThrow();
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
            JexxaLogger.getLogger(getClass()).warn("More than one constructor available. => Reconsider to provide only a single constructor");
        }

        return constructorList.stream().
                filter(constructor -> drivenAdapterFactory.validateAdaptersAvailable(Arrays.asList(constructor.getParameterTypes()))).
                findFirst();
    }


    public List<Object> createPortsBy(Class <? extends Annotation> portAnnotation, Properties drivenAdapterProperties) {
        var annotationScanner = new DependencyScanner().
                whiteListPackages(whiteListPackages);

        var scannedInboundPorts = annotationScanner.getClassesWithAnnotation(portAnnotation);

        var result = new ArrayList<>();
        scannedInboundPorts.
            forEach(element -> result.add(createByType(element, drivenAdapterProperties)));

        return result;
    }
    

    private Object[] createDrivenAdapterForConstructor(Constructor<?> portConstructor, Properties drivenAdapterProperties)
    {
        var objectList = new ArrayList<>();

        for ( int i = 0; i < portConstructor.getParameterTypes().length; ++i )
        {
            try
            {
                objectList.add( drivenAdapterFactory.create(portConstructor.getParameterTypes()[i], drivenAdapterProperties) );
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
