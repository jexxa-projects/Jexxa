package io.jexxa.test.messaging;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import io.jexxa.utils.annotations.CheckReturnValue;

public class MessageRecorderProducer extends MessageProducer
{
    protected <T> MessageRecorderProducer(T message, MessageRecorderStrategy jmsSender)
    {
        super(message, jmsSender);
    }

}
