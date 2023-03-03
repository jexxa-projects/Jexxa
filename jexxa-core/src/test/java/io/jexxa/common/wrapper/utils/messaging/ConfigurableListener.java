package io.jexxa.common.wrapper.utils.messaging;

import io.jexxa.drivingadapter.messaging.DefaultJMSConfiguration;
import io.jexxa.drivingadapter.messaging.JMSConfiguration;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.ArrayList;
import java.util.List;

public class ConfigurableListener implements MessageListener
{
    private final List<Message> messageList = new ArrayList<>();
    private final String topicDestination;
    private final JMSConfiguration.MessagingType messagingType;

    public ConfigurableListener(String topicDestination, JMSConfiguration.MessagingType messagingType)
    {
        this.topicDestination = topicDestination;
        this.messagingType = messagingType;
    }
    public void onMessage(Message message)
    {
        messageList.add(message);
    }

    public List<Message> getMessages()
    {
        return messageList;
    }

    @SuppressWarnings("unused") // Used by JMSAdapter
    public JMSConfiguration getConfiguration()
    {
        return new DefaultJMSConfiguration(topicDestination, messagingType);
    }
}
