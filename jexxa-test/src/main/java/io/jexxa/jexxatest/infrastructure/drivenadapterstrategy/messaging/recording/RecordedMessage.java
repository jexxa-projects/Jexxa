package io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;

import java.util.Properties;

/**
 * Stores a message that is sent via JMS messaging API
 */
public class RecordedMessage
{
    private final String serializedMessage;
    private final Object message;
    private final MessageProducer.DestinationType destinationType;
    private final Properties messageProperties;
    private final String destinationName;
    private final MessageSender.MessageType messageType;


    RecordedMessage(
            Object message,
            String serializedMessage,
            MessageProducer.DestinationType destinationType,
            String destinationName,
            Properties messageProperties,
            MessageSender.MessageType messageType)
    {
        this.message = message;
        this.serializedMessage = serializedMessage;
        this.destinationType = destinationType;
        this.destinationName = destinationName;
        this.messageProperties = messageProperties;
        this.messageType = messageType;
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

    public MessageSender.MessageType getMessageType() { return messageType; }
}

