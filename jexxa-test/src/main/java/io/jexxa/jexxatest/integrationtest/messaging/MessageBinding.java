package io.jexxa.jexxatest.integrationtest.messaging;


import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.common.drivenadapter.messaging.MessageSenderFactory;
import io.jexxa.common.drivenadapter.messaging.jms.JMSSender;
import io.jexxa.common.drivingadapter.messaging.jms.JMSAdapter;
import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.jexxa.common.drivenadapter.messaging.MessageSenderFactory.createMessageSender;

public class MessageBinding implements AutoCloseable
{
    private final List<JMSAdapter> adapterList = new ArrayList<>();
    private final Properties properties;
    private final MessageSender messageSender;


    public MessageBinding(Class<?> application, Properties properties)
    {
        MessageSenderFactory.setMessageSender(JMSSender.class, application);
        this.properties = properties;
        this.messageSender = createMessageSender(application, properties);
    }

    public MessageSender getMessageSender()
    {
        return messageSender;
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
