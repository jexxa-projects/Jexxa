package io.jexxa.test.messaging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.utils.annotations.CheckReturnValue;

public class MessageRecorderStrategy  extends MessageSender
{
    private Object currentMessage;
    private MessageRecorder messageRecorder;

    @CheckReturnValue
    @Override
    public <T> MessageProducer send(T message)
    {
        StackWalker walker = StackWalker
                .getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

        Class<?> callerClass = walker.getCallerClass();

        currentMessage = message;
        messageRecorder = MessageRecordingSystem.getInstance().getMessageRecorder(callerClass);

        return new MessageRecorderProducer(message, this);
    }

    @Override
    protected void sendMessageToQueue(String message, String destination, Properties messageProperties)
    {
        messageRecorder.putMessage(new MessageRecorder.RecordedMessage(
                currentMessage,
                message,
                MessageProducer.DestinationType.QUEUE,
                destination,
                messageProperties)
        );
    }

    @Override
    protected void sendMessageToTopic(String message, String destination, Properties messageProperties)
    {
        messageRecorder.putMessage(new MessageRecorder.RecordedMessage(
                currentMessage,
                message,
                MessageProducer.DestinationType.TOPIC,
                destination,
                messageProperties)
        );
    }
}
