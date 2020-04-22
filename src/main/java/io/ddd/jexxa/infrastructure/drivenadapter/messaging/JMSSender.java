package io.ddd.jexxa.infrastructure.drivenadapter.messaging;


import java.util.Map;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.Gson;

/**
 *
 */
public class JMSSender
{
    public static final String JNDI_PROVIDER_URL_KEY = "java.naming.provider.url";
    public static final String JNDI_USER_KEY = "java.naming.user";
    public static final String JNDI_PASSWORD_KEY = "java.naming.password";
    public static final String JNDI_FACTORY_KEY = "java.naming.factory.initial";


    public static final String DEFAULT_JNDI_PROVIDER_URL = "tcp://localhost:61616";
    public static final String DEFAULT_JNDI_USER = "admin";
    public static final String DEFAULT_JNDI_PASSWORD = "admin";
    public static final String DEFAULT_JNDI_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private final Properties properties;

    public JMSSender(final Properties properties)
    {
        this.properties = properties;
    }
    
    public void sendToTopic(Object message, final String topicName, final Properties messageProperties)
    {
        try(Connection connection = createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
        )
        {
            var destination = session.createTopic(topicName);
            try (var producer = session.createProducer(destination) )
            {
                sendMessage(message, producer, session, messageProperties);
            }
        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Could not send message ", e);
        }
    }

    public void sendToQueue(final Object message, final String queueName, Properties messageProperties)
    {
        try (final Connection connection = createConnection();
             final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            var destination = session.createQueue(queueName);
            try (var producer = session.createProducer(destination) )
            {
                sendMessage(message, producer, session, messageProperties);
            }
        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Exception beim Senden der Message", e);
        }
    }

    private void sendMessage(final Object message, final MessageProducer messageProducer, final Session session, Properties messageProperties) throws JMSException
    {
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        var gson = new Gson();
        var textMessage = session.createTextMessage(gson.toJson(message));

        if (messageProperties != null)
        {
            for (Map.Entry<Object, Object> entry : messageProperties.entrySet())
            {
                textMessage.setStringProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        messageProducer.send(textMessage);
    }

    @SuppressWarnings("DuplicatedCode")
    Connection createConnection()
    {
        //noinspection DuplicatedCode
        try
        {
            final InitialContext initialContext = new InitialContext(properties);
            final ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            return connectionFactory.createConnection(properties.getProperty(JNDI_USER_KEY), properties.getProperty(JNDI_PASSWORD_KEY));
        }
        catch (NamingException e)
        {
            throw new IllegalStateException("No ConnectionFactory available via : " + properties.get(JNDI_PROVIDER_URL_KEY), e);
        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Can not connect to " + properties.get(JNDI_PROVIDER_URL_KEY), e);
        }
    }
}
