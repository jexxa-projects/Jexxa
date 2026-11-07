package io.jexxa.jexxatest.integrationtest.messaging;



import io.jexxa.adapters.core.drivenadapter.messaging.MessageSender;
import io.jexxa.adapters.core.drivenadapter.messaging.MessageSenderFactory;
import io.jexxa.adapters.jms.drivenadapter.JMSSender;
import io.jexxa.adapters.jms.drivingadapter.JMSAdapter;
import io.jexxa.adapters.jms.drivingadapter.JMSConfiguration;
import io.jexxa.jexxatest.integrationtest.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static io.jexxa.adapters.core.drivenadapter.messaging.MessageSenderFactory.createMessageSender;


public class JMSBinding implements AutoCloseable
{
    private final List<JMSAdapter> adapterList = new ArrayList<>();
    private final Properties properties;
    private final MessageSender messageSender;


    public JMSBinding(Class<?> application, Properties properties)
    {
        MessageSenderFactory.setMessageSender(JMSSender.class, application);
        this.properties = properties;
        this.messageSender = createMessageSender(application, properties);
    }

    public MessageSender getSender()
    {
        return messageSender;
    }

    public Listener getListener(String destination, JMSConfiguration.MessagingType messagingType)
    {
        var jmsListener = new JMSListener(destination, messagingType);
        var jmsAdapter = new JMSAdapter(properties);
        jmsAdapter.register(jmsListener);
        jmsAdapter.start();
        adapterList.add(jmsAdapter);

        return jmsListener;
    }

    public void registerListener(Listener listener)
    {
        var jmsAdapter = new JMSAdapter(properties);
        jmsAdapter.register(listener);
        jmsAdapter.start();
        adapterList.add(jmsAdapter);
    }

    @Override
    public void close()  {
        adapterList.forEach(JMSAdapter::stop);
        adapterList.clear();
    }

}
