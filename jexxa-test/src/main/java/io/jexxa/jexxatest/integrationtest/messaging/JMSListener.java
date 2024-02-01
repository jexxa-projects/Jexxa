package io.jexxa.jexxatest.integrationtest.messaging;

import io.jexxa.common.drivingadapter.messaging.jms.DefaultJMSConfiguration;
import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;
import io.jexxa.common.drivingadapter.messaging.jms.listener.JSONMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class JMSListener extends JSONMessageListener implements MessageListener  {
    private final List<String> messageList = new ArrayList<>();
    private final String topicDestination;
    private final JMSConfiguration.MessagingType messagingType;

    public JMSListener(String topicDestination, JMSConfiguration.MessagingType messagingType)
    {
        this.topicDestination = topicDestination;
        this.messagingType = messagingType;
    }

    @Override
    public void onMessage(String message)
    {
        messageList.add(message);
    }

    @Override
    public List<String> getAll()
    {
        return messageList;
    }

    @Override
    public void clear()
    {
        messageList.clear();
    }
    @Override
    public <T> T pop(Class<T> clazz)
    {
        if (messageList.isEmpty())
        {
            return null;
        }

        return fromJson( messageList.remove(0), clazz);
    }

    @Override
    public JMSListener awaitMessage(int timeout, TimeUnit timeUnit)
    {
        await().atMost(timeout, timeUnit)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .until(() -> !getAll().isEmpty());

        return this;
    }

    @SuppressWarnings("unused") // Used by JMSAdapter
    public JMSConfiguration getConfiguration()
    {
        return new DefaultJMSConfiguration(topicDestination, messagingType);
    }
}
