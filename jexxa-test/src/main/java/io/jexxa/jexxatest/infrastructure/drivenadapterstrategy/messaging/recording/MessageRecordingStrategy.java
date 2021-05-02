package io.jexxa.jexxatest.infrastructure.drivenadapterstrategy.messaging.recording;

import java.util.Objects;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.utils.annotations.CheckReturnValue;

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
    protected void sendToQueue(String message, String destination, Properties messageProperties)
    {
        messageRecorder.put(new RecordedMessage(
                currentMessage,
                message,
                MessageProducer.DestinationType.QUEUE,
                destination,
                messageProperties)
        );
    }

    @Override
    protected void sendToTopic(String message, String destination, Properties messageProperties)
    {
        messageRecorder.put(new RecordedMessage(
                currentMessage,
                message,
                MessageProducer.DestinationType.TOPIC,
                destination,
                messageProperties)
        );
    }

    private static class RecordableMessageProducer extends MessageProducer
    {
        protected <T> RecordableMessageProducer(T message, MessageRecordingStrategy jmsSender)
        {
            super(message, jmsSender);
        }
    }
}
