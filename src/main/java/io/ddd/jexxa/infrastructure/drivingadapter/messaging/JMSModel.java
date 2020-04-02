package io.ddd.jexxa.infrastructure.drivingadapter.messaging;

import java.util.Properties;

public class JMSModel
{
    private final Object object;
    private final Properties properties;

    public JMSModel(Object object, Properties properties)
    {
        this.object = object;
        this.properties = properties;
    }

    boolean isTopic()
    {
        return true;
    }

    boolean isQueue()
    {
        return false;
    }

    String getDesination()
    {
        return object.getClass().getSimpleName();
    }

}
