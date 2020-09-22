package io.jexxa.test.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;

public class MessageRecorder
{
    public static class RecordedMessage
    {
        private final String serializedMessage;
        private final Object message;
        private final MessageProducer.DestinationType destinationType;
        private final Properties messageProperties;
        private final String destinationName;

        public RecordedMessage(
                Object message,
                String serializedMessage,
                MessageProducer.DestinationType destinationType,
                String destinationName,
                Properties messageProperties)
        {
            this.message = message;
            this.serializedMessage = serializedMessage;
            this.destinationType = destinationType;
            this.destinationName = destinationName;
            this.messageProperties = messageProperties;
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
            return type.cast( message );
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
    }

    private final List<RecordedMessage> recordedMessageList= new ArrayList<>();

    void putMessage(RecordedMessage recordedMessage)
    {
        recordedMessageList.add(recordedMessage);
    }

    public List<MessageRecorder.RecordedMessage> getMessages()
    {
        return recordedMessageList;
    }

    public RecordedMessage pop()
    {
        if (recordedMessageList.isEmpty())
        {
            return null;
        }

        var latestElement = recordedMessageList.get(0);
        recordedMessageList.remove(0);
        return latestElement;
    }
}
