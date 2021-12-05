package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import java.util.Objects;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public abstract class TypedMessageListener<T> extends JSONMessageListener
{
    private final Class<T> clazz;

    protected TypedMessageListener(Class<T> clazz)
    {
        this.clazz = Objects.requireNonNull( clazz );
    }

    public abstract void onMessage(T message);

    @Override
    public final void onMessage(String message)
    {
        try
        {
            onMessage( fromJson(message, clazz ));
        }
        catch (RuntimeException exception)
        {
            JexxaLogger.getLogger(getClass()).error(exception.getMessage());
            JexxaLogger.getLogger(getClass()).error("Message : {}", message);
        }
    }

    protected static <U> U fromJson( String message, Class<U> clazz)
    {
        return getJSONConverter().fromJson( message, clazz);
    }

}
