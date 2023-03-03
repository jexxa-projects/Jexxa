package io.jexxa.jexxatest.integrationtest.messaging;

import io.jexxa.drivenadapter.strategy.messaging.MessageSender;
import io.jexxa.drivenadapter.strategy.messaging.MessageSenderManager;
import io.jexxa.drivingadapter.messaging.JMSAdapter;
import io.jexxa.drivingadapter.messaging.JMSConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MessageBinding implements AutoCloseable
{
    private final List<JMSAdapter> adapterList = new ArrayList<>();
    private final Properties properties;
    private final Class<?> application;


    public MessageBinding(Class<?> application, Properties properties)
    {
        this.application = application;
        this.properties = properties;

    }
    public MessageSender getMessageSender()
    {
        return MessageSenderManager.getMessageSender(application, properties);
    }

    public MessageListener getMessageListener(String destination, JMSConfiguration.MessagingType messagingType)
    {
        var jmsListener = new JMSListener(destination, messagingType);
        var jmsAdapter = new JMSAdapter(properties);
        jmsAdapter.register(jmsListener);
        jmsAdapter.start();
        adapterList.add(jmsAdapter);

        return jmsListener;
    }

    public void registerMessageListener(MessageListener messageListener)
    {
        var jmsAdapter = new JMSAdapter(properties);
        jmsAdapter.register(messageListener);
        jmsAdapter.start();
        adapterList.add(jmsAdapter);
    }

    @Override
    public void close()  {
        adapterList.forEach(JMSAdapter::stop);
        adapterList.clear();
    }
}
