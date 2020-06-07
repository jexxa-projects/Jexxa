package io.jexxa.infrastructure.utils.messaging;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.utils.JexxaLogger;

public class TopicListener implements MessageListener
{
    public static final String TOPIC_DESTINATION = "JEXXA_TOPIC";

    private final List<Message> messageList = new ArrayList<>();

    @Override
    @JMSConfiguration(destination = TOPIC_DESTINATION, messagingType = JMSConfiguration.MessagingType.TOPIC)
    public void onMessage(Message message)
    {
        try
        {
            JexxaLogger.getLogger(TopicListener.class).info(((TextMessage) message).getText());
            messageList.add(message);
        }
        catch ( JMSException e) {
            JexxaLogger.getLogger(TopicListener.class).error(e.getMessage());
        }
    }

    public List<Message> getMessages()
    {
        return messageList;
    }
}