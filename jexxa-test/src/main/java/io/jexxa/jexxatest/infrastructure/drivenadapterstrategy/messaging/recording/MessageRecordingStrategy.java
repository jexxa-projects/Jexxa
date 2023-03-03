package io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording;

import io.jexxa.pattern.messaging.MessageProducer;
import io.jexxa.pattern.messaging.MessageSender;
import io.jexxa.common.annotation.CheckReturnValue;

import java.util.Objects;
import java.util.Properties;

public class MessageRecordingStrategy extends MessageSender
{
    private Object currentMessage;
    private MessageRecorder messageRecorder;

    @CheckReturnValue
    @Override
    public <T> MessageProducer send(T message)
    {
        Objects.requireNonNull(message);

        //Get caller object of this class. Here we assume that it is the implementation of a driven adapter
        var walker = StackWalker
                .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        Class<?> callerClass = walker.getCallerClass();

        currentMessage = message;
        messageRecorder = MessageRecorderManager.getMessageRecorder(callerClass);
        return new RecordableMessageProducer(message, this);
    }

    @Override
    protected void sendToQueue(String message, String destination, Properties messageProperties, MessageType messageType)
    {
        messageRecorder.put(new RecordedMessage(
                currentMessage,
                message,
                MessageProducer.DestinationType.QUEUE,
                destination,
                messageProperties,
                messageType)
        );
    }

    @Override
    protected void sendToTopic(String message, String destination, Properties messageProperties, MessageType messageType)
    {
        messageRecorder.put(new RecordedMessage(
                currentMessage,
                message,
                MessageProducer.DestinationType.TOPIC,
                destination,
                messageProperties,
                messageType)
        );
    }

    private static class RecordableMessageProducer extends MessageProducer
    {
        protected <T> RecordableMessageProducer(T message, MessageRecordingStrategy jmsSender)
        {
            super(message, jmsSender, MessageType.TEXT_MESSAGE);
        }
    }
}
