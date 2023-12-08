package io.jexxa.drivingadapter.messaging.listener;

import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

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
