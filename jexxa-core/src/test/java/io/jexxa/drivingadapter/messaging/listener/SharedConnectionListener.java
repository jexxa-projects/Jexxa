package io.jexxa.drivingadapter.messaging.listener;

import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

public class SharedConnectionListener implements MessageListener
{
    public static final String TOPIC_DESTINATION = "JEXXA_TOPIC";

    private final List<Message> messageList = new ArrayList<>();

    @Override
    @JMSConfiguration(destination = TOPIC_DESTINATION, messagingType = JMSConfiguration.MessagingType.TOPIC, sharedSubscriptionName = "SharedConnection", durable = JMSConfiguration.DurableType.NON_DURABLE)
    public void onMessage(Message message)
    {
        messageList.add(message);
    }

    public List<Message> getMessages()
    {
        return messageList;
    }
    public int getMessageCount() {return messageList.size(); }
}