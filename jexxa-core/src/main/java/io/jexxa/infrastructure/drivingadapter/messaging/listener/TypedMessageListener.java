package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import io.jexxa.utils.JexxaLogger;

@SuppressWarnings("unused")
public abstract class TypedMessageListener<T> extends JSONMessageListener
{
    private final Class<T> clazz;

    protected TypedMessageListener(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    public abstract void onMessage(T message);

    @Override
    public final void onMessage(TextMessage message)
    {
        String currentText = null;
        try
        {
            currentText = message.getText();
            onMessage( fromJson(currentText, clazz ));
        }
        catch (RuntimeException | JMSException exception)
        {
            JexxaLogger.getLogger(TypedMessageListener.class).error(exception.getMessage());
            JexxaLogger.getLogger(TypedMessageListener.class).error("Message : {}", currentText);
        }
    }

    protected static <U> U fromJson( String message, Class<U> clazz)
    {
        return getJSONConverter().fromJson( message, clazz);
    }

}
