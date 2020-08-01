package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;

import io.jexxa.utils.CheckReturnValue;

public abstract class MessageSender
{
    @CheckReturnValue
    public <T> MessageProducer send(T message)
    {
        return new MessageProducer(message, this);
    }

    
    protected abstract void sendMessageToQueue(String message, String destination, Properties properties);

    protected abstract void sendMessageToTopic(String message, String destination, Properties properties);
}
