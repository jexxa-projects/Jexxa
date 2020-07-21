package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;

import jdk.jfr.Experimental;

@Experimental
public abstract class MessageSender
{
    public <T> MessageProducer send(T message)
    {
        return new MessageProducer(message, this);
    }

    
    protected abstract void sendMessage(String message, MessageProducer.Queue queue, Properties properties);

    protected abstract void sendMessage(String message, MessageProducer.Topic topic, Properties properties);
}
