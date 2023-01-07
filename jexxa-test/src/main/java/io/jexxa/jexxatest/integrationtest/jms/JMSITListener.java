package io.jexxa.jexxatest.integrationtest.jms;

import io.jexxa.infrastructure.drivingadapter.messaging.DefaultJMSConfiguration;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.infrastructure.drivingadapter.messaging.listener.JSONMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class JMSITListener extends JSONMessageListener {
    private final List<String> messageList = new ArrayList<>();
    private final String topicDestination;
    private final JMSConfiguration.MessagingType messagingType;

    public JMSITListener(String topicDestination, JMSConfiguration.MessagingType messagingType)
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
    public void clearMessages()
    {
        messageList.clear();
    }

    @SuppressWarnings("unused")
    public <T> T getMessage(Class<T> clazz)
    {
        if (messageList.isEmpty())
        {
            return null;
        }

        return fromJson( messageList.remove(0), clazz);
    }

    @SuppressWarnings("unused")
    public void waitUntilMessageReceived(int timeout, TimeUnit timeUnit)
    {
        await().atMost(timeout, timeUnit)
                .pollDelay(100, TimeUnit.MILLISECONDS)
                .until(() -> !getMessages().isEmpty());
    }

    @SuppressWarnings("unused") // Used by JMSAdapter
    public JMSConfiguration getConfiguration()
    {
        return new DefaultJMSConfiguration(topicDestination, messagingType);
    }
}
