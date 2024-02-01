package io.jexxa.jexxatest.infrastructure.messaging.recording;

import io.jexxa.common.drivenadapter.messaging.DestinationType;
import io.jexxa.common.drivenadapter.messaging.MessageSender;

import java.util.Properties;

/**
 * Stores a message that is sent via JMS messaging API
 */
public record RecordedMessage(Object message,
                              String serializedMessage,
                              DestinationType destinationType,
                              String destinationName,
                              Properties messageProperties,
                              MessageSender.MessageType messageType)
{
    public <T> T getMessage(Class<T> type) {
        return type.cast(message());
    }
}

