package io.jexxa.test.messaging;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;

public class RecordableMessageProducer extends MessageProducer
{
    protected <T> RecordableMessageProducer(T message, MessageRecorderStrategy jmsSender)
    {
        super(message, jmsSender);
    }
}
