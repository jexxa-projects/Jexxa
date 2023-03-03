package io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording;

import io.jexxa.infrastructure.messaging.MessageProducer;
import io.jexxa.infrastructure.messaging.MessageSender;

import java.util.Properties;

/**
 * Stores a message that is sent via JMS messaging API
 */
public record RecordedMessage(Object message,
                              String serializedMessage,
                              MessageProducer.DestinationType destinationType,
                              String destinationName,
                              Properties messageProperties,
                              MessageSender.MessageType messageType)
{
    public <T> T getMessage(Class<T> type) {
        return type.cast(message());
    }
}

