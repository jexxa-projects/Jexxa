package io.jexxa.jexxatest.integrationtest.messaging;

import io.jexxa.infrastructure.drivingadapter.messaging.DefaultJMSConfiguration;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.infrastructure.drivingadapter.messaging.listener.JSONMessageListener;

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
    public void onMessage(String message)
    {
        messageList.add(message);
    }

    public List<String> getMessages()
    {
        return messageList;
    }

    @SuppressWarnings("unused")
    public void clear()
    {
        messageList.clear();
    }

    @SuppressWarnings("unused")
    public <T> T pop(Class<T> clazz)
    {
        if (messageList.isEmpty())
        {
            return null;
        }

        return fromJson( messageList.remove(0), clazz);
    }

    @SuppressWarnings("unused")
    public JMSListener waitUntilMessageReceived(int timeout, TimeUnit timeUnit)
    {
        await().atMost(timeout, timeUnit)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .until(() -> !getMessages().isEmpty());

        return this;
    }

    @SuppressWarnings("unused") // Used by JMSAdapter
    public JMSConfiguration getConfiguration()
    {
        return new DefaultJMSConfiguration(topicDestination, messagingType);
    }
}
