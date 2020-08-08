package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import java.util.Properties;

import io.jexxa.utils.annotations.CheckReturnValue;

public abstract class MessageSender
{
    @CheckReturnValue
    public <T> MessageProducer send(T message)
    {
        return new MessageProducer(message, this);
    }


    /**
     * Sends an asynchronous text message to a queue
     *
     * @param message message as string. Must not be null
     * @param destination name of the queue to send the message
     * @param messageProperties additional properties of the message. Can be null if no properties are required 
     */
    protected abstract void sendMessageToQueue(String message, String destination, Properties messageProperties);

    /**
     * Sends an asynchronous text message to a topic
     *
     * @param message message as string. Must not be null
     * @param destination name of the queue to send the message
     * @param messageProperties additional properties of the message. Can be null if no properties are required
     */
    protected abstract void sendMessageToTopic(String message, String destination, Properties messageProperties);
}
