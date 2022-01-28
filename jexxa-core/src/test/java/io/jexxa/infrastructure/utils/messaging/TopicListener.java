package io.jexxa.infrastructure.utils.messaging;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

public class TopicListener implements MessageListener
{
    public static final String TOPIC_DESTINATION = "JEXXA_TOPIC";

    private final List<Message> messageList = new ArrayList<>();

    @Override
    @JMSConfiguration(destination = TOPIC_DESTINATION, messagingType = JMSConfiguration.MessagingType.TOPIC)
    public void onMessage(Message message)
    {
        messageList.add(message);
    }

    public List<Message> getMessages()
    {
        return messageList;
    }
}