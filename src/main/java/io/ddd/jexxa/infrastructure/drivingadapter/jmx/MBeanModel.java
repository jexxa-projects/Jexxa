package io.ddd.jexxa.infrastructure.drivingadapter.jmx;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class MBeanModel implements DynamicMBean
{
    private Object object;

    MBeanModel(Object object)
    {
        this.object = object;
    }


    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException
    {
        return null;  // We don't offer access to attributes
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
    {
        //TODO: Throw an exception
    }

    @Override
    public AttributeList getAttributes(String[] attributes)
    {
        return null; //We don't offer access to attributes
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes)
    {
        return null; //TODO: Throw an exception since we don't offer access to attributes
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException
    {
        return null; // TODO: This method must be implemented
    }

    public MBeanInfo getMBeanInfo() {
        //TODO: Update info on operations
        MBeanInfo mBeanInfo = new MBeanInfo(
                object.getClass().getSimpleName(),
                "Hello Jexxa",
                null,
                null,
                null,
                null
        );

        return mBeanInfo;
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



}
