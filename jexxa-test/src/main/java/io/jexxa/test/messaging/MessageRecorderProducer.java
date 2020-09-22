package io.jexxa.test.messaging;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;

public class MessageRecorderProducer extends MessageProducer
{
    protected <T> MessageRecorderProducer(T message, MessageRecorderStrategy jmsSender)
    {
        super(message, jmsSender);
    }

}
