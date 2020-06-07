package io.jexxa.infrastructure.utils.messaging;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.utils.JexxaLogger;

public class QueueListener implements MessageListener
{
    public static final String QUEUE_DESTINATION = "JEXXA_QUEUE";

    private final List<Message> messageList = new ArrayList<>();

    @Override
    @JMSConfiguration(destination = QUEUE_DESTINATION, messagingType = JMSConfiguration.MessagingType.QUEUE)
    public void onMessage(Message message)
    {
        try
        {
            JexxaLogger.getLogger(QueueListener.class).info(((TextMessage) message).getText());
            messageList.add(message);
        }
        catch ( JMSException e) {
            JexxaLogger.getLogger(QueueListener.class).error(e.getMessage());
        }
    }

    public List<Message> getMessages()
    {
        return messageList;
    }
}
