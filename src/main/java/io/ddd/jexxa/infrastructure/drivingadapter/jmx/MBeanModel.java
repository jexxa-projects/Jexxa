package io.ddd.jexxa.infrastructure.drivingadapter.jmx;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;

import io.ddd.jexxa.utils.JexxaLogger;

public class MBeanModel implements DynamicMBean
{
    public static final String CONTEXT_NAME = "io.ddd.jexxa.context.name";

    private final Object object;
    String contextName;
    
    MBeanModel(Object object, Properties properties)
    {
        this.object = object;

        if ( properties != null)
        {
            contextName = properties.getProperty(CONTEXT_NAME);
        }

        if ( contextName == null ) {
            contextName = "UnknownContext";
        }
    }


    @Override
    public Object getAttribute(String attribute)
    {
        return null;  // We don't offer access to attributes
    }

    @Override
    public void setAttribute(Attribute attribute)
    {
        throw new UnsupportedOperationException();    //We don't offer access to attributes
    }

    @Override
    public AttributeList getAttributes(String[] attributes)
    {
        return new AttributeList(); //We don't offer access to attributes
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes)
    {
        throw new UnsupportedOperationException();    //We don't offer access to attributes
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature)
    {
        var method = getMethod(actionName);
        if (method.isPresent())
        {
            try
            {
                return method.get().invoke(object, params);
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                JexxaLogger.getLogger(getClass()).error(e.getMessage());
            }
        }

        return null;
    }

    public MBeanInfo getMBeanInfo() {
        //TODO: Update info on operations
        return new MBeanInfo(
                object.getClass().getSimpleName(),
                "Hello Jexxa",
                null,                            
                null,
                getMBeanOperation(),
                null
        );

    }

    public ObjectName getObjectName()
    {
        try
        {
           return new ObjectName(getDomainPath());
        } catch (Exception e)
        {
           throw new IllegalArgumentException(e.getMessage());
        }
    }

    MBeanOperationInfo[] getMBeanOperation()
    {
        var methodList = Arrays.stream(object.getClass().getMethods()).
                collect(Collectors.toList());

        //Get methods only from concrete type => Exclude all methods from Object
        methodList.removeAll(Arrays.asList(Object.class.getMethods()));

        return methodList.
                stream().
                map(element -> new MBeanOperationInfo(element.getName(), element)).
                toArray(MBeanOperationInfo[]::new);
    }

    String getDomainPath()
    {
        //Build domainPath for jmx as follows: <ContextName> -> <Annotation of object (if available) > -> <simple name of object>
        var stringBuilder = new StringBuilder();

        stringBuilder.
                append(contextName).
                append(":");

        getFirstAnnotation().ifPresent(
                annotation -> stringBuilder.
                append("type=").
                append(annotation.annotationType().getSimpleName()).
                append(",")
        );

        stringBuilder.
                append("name=").
                append(object.getClass().getSimpleName());

        return stringBuilder.toString();
    }

    private Optional<Method> getMethod(String name)
    {
        return Arrays.
                stream(object.getClass().getMethods()).
                filter(method -> method.getName().equals(name)).
                findFirst();
    }

    private Optional<Annotation> getFirstAnnotation()
    {
        return Arrays.
                stream(object.getClass().getDeclaredAnnotations()).
                findFirst();
    }

}
