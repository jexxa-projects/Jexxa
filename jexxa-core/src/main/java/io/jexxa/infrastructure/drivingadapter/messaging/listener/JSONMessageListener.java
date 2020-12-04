package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.google.gson.Gson;
import io.jexxa.utils.JexxaLogger;

public abstract class JSONMessageListener<T>  implements MessageListener
{
    private static final Gson gson = new Gson();
    private final Class<T> clazz;
    private TextMessage currentMessage;

    protected JSONMessageListener(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    public abstract void onMessage(T message);

    @Override
    public final void onMessage(Message message)
    {
        String textMessage = null;
        try
        {
            this.currentMessage = (TextMessage) message;
            textMessage = currentMessage.getText();
            onMessage( fromJson( textMessage, clazz ));
        }
        catch (RuntimeException | JMSException exception)
        {
            JexxaLogger.getLogger(JSONMessageListener.class).error(exception.getMessage());
            JexxaLogger.getLogger(JSONMessageListener.class).error("Message : {}", textMessage);
        }
        currentMessage = null;
    }

    protected final TextMessage getCurrentMessage()
    {
        return currentMessage;
    }

    protected static <U> U fromJson( String message, Class<U> clazz)
    {
        return gson.fromJson( message, clazz);
    }

}
