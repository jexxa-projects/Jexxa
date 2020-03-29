package io.ddd.jexxa.infrastructure.drivingadapter.jmx;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectName;

import io.ddd.jexxa.utils.JexxaLogger;

public class MBeanModel implements DynamicMBean
{
    private Object object;

    MBeanModel(Object object)
    {
        this.object = object;
    }


    @Override
    public Object getAttribute(String attribute)
    {
        return null;  // We don't offer access to attributes
    }

    @Override
    public void setAttribute(Attribute attribute)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttributeList getAttributes(String[] attributes)
    {
        return new AttributeList(); //We don't offer access to attributes
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes)
    {
        throw new UnsupportedOperationException();
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
           return new ObjectName("com.example:type=" + object.getClass().getSimpleName());
        } catch (Exception e)
        {
           throw new IllegalArgumentException(e.getMessage());
        }
    }

    MBeanOperationInfo[] getMBeanOperation()
    {
        //find methods with no arguments in a first step
        return Arrays.
                stream(object.getClass().getMethods()).
                filter(method -> method.getParameterCount() == 0).
                map(element -> new MBeanOperationInfo(element.getName(), element)).
                toArray(MBeanOperationInfo[]::new);
    }

    Optional<Method> getMethod(String name)
    {
        return Arrays.
                stream(object.getClass().getMethods()).
                filter(method -> method.getName().equals(name)).
                findFirst();
    }



}
