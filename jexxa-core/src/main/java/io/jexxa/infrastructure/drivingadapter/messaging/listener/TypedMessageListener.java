package io.jexxa.infrastructure.drivingadapter.messaging.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.google.gson.Gson;
import io.jexxa.utils.JexxaLogger;

public abstract class TypedMessageListener<T>  implements MessageListener
{
    private final Gson gson = new Gson();
    private final Class<T> clazz;

    protected TypedMessageListener(Class<T> clazz)
    {
        this.clazz = clazz;
    }

    public abstract void onMessage(T message);

    @Override
    public final void onMessage(Message message)
    {
        try
        {
            onMessage( createMessage( message ));
        }
        catch (RuntimeException | JMSException exception)
        {
            JexxaLogger.getLogger(DomainEventListener.class).error(exception.getMessage());
        }
    }

    private T createMessage( Message message) throws JMSException
    {
        TextMessage textMessage = (TextMessage) message;
        return gson.fromJson(textMessage.getText(), clazz);
    }

    protected Gson getGson()
    {
        return gson;
    }
}
