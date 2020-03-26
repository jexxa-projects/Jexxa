package io.ddd.jexxa.infrastructure.drivingadapter.jmx;


import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.DynamicMBean;
import javax.management.MBeanInfo;
import javax.management.ObjectName;

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
        return null; // TODO: This method must be implemented
    }

    public MBeanInfo getMBeanInfo() {
        //TODO: Update info on operations
        return new MBeanInfo(
                object.getClass().getSimpleName(),
                "Hello Jexxa",
                null,
                null,
                null,
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



}
