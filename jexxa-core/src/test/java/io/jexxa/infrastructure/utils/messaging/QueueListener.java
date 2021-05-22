package io.jexxa.infrastructure.utils.messaging;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;
import javax.jms.MessageListener;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;

public class QueueListener implements MessageListener
{
    public static final String QUEUE_DESTINATION = "JEXXA_QUEUE";

    private final List<Message> messageList = new ArrayList<>();

    @Override
    @JMSConfiguration(destination = QUEUE_DESTINATION, messagingType = JMSConfiguration.MessagingType.QUEUE)
    public void onMessage(Message message)
    {
        messageList.add(message);
    }

    public List<Message> getMessages()
    {
        return messageList;
    }
}
