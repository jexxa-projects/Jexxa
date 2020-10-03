package io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;

/**
 * Stores a message that is send via JMS messaging API
 */
public class RecordedMessage
{
    private final String serializedMessage;
    private final Object message;
    private final MessageProducer.DestinationType destinationType;
    private final Properties messageProperties;
    private final String destinationName;


    RecordedMessage(
            Object message,
            String serializedMessage,
            MessageProducer.DestinationType destinationType,
            String destinationName,
            Properties messageProperties)
    {
        this.message = message;
        this.serializedMessage = serializedMessage;
        this.destinationType = destinationType;
        this.destinationName = destinationName;
        this.messageProperties = messageProperties;
    }

    public String getSerializedMessage()
    {
        return serializedMessage;
    }

    public Object getMessage()
    {
        return message;
    }

    public <T> T getMessage(Class<T> type)
    {
        return type.cast( getMessage() );
    }

    public MessageProducer.DestinationType getDestinationType()
    {
        return destinationType;
    }

    public Properties getMessageProperties()
    {
        return messageProperties;
    }

    public String getDestinationName()
    {
        return destinationName;
    }
}

