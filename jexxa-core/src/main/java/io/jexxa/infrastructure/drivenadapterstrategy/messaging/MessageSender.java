package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;

public abstract class MessageSender
{
    public <T> MessageProducer send(T message)
    {
        return new MessageProducer(message, this);
    }

    
    protected abstract void sendMessageToQueue(String message, String destination, Properties properties);

    protected abstract void sendMessageToTopic(String message, String destination, Properties properties);
}
