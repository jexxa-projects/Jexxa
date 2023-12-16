package io.jexxa.jexxatest.infrastructure.messaging.recording;

import io.jexxa.common.facade.utils.annotation.CheckReturnValue;
import io.jexxa.common.drivenadapter.messaging.DestinationType;
import io.jexxa.common.drivenadapter.messaging.MessageBuilder;
import io.jexxa.common.drivenadapter.messaging.MessageSender;

import java.util.Objects;
import java.util.Properties;

public class MessageRecordingStrategy extends MessageSender
{
    private Object currentMessage;
    private MessageRecorder messageRecorder;

    @CheckReturnValue
    @Override
    public <T> MessageBuilder send(T message)
    {
        Objects.requireNonNull(message);

        //Get a caller object of this class. Here we assume that it is the implementation of a driven adapter
        var walker = StackWalker
                .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        Class<?> callerClass = walker.getCallerClass();

        currentMessage = message;
        messageRecorder = MessageRecorderManager.getMessageRecorder(callerClass);
        return new RecordableMessageBuilder(message, this);
    }

    @Override
    protected void sendToQueue(String message, String destination, Properties messageProperties, MessageType messageType)
    {
        messageRecorder.put(new RecordedMessage(
                currentMessage,
                message,
                DestinationType.QUEUE,
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
                DestinationType.TOPIC,
                destination,
                messageProperties,
                messageType)
        );
    }

    private static class RecordableMessageBuilder extends MessageBuilder
    {
        protected <T> RecordableMessageBuilder(T message, MessageRecordingStrategy jmsSender)
        {
            super(message, jmsSender, MessageType.TEXT_MESSAGE);
        }
    }
}
