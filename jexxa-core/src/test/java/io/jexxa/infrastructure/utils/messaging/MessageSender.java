package io.jexxa.infrastructure.utils.messaging;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.utils.JexxaLogger;

public class MessageSender implements AutoCloseable
{
    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;

    public MessageSender(Properties properties, String messageDestination, JMSConfiguration.MessagingType messagingType)
    {
        try
        {
            this.connection = JMSAdapter.createConnection(properties);
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination;
            if (messagingType == JMSConfiguration.MessagingType.TOPIC)
            {
                destination = session.createTopic(messageDestination);
            }
            else
            {
                destination = session.createQueue(messageDestination);
            }


            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        } catch (JMSException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public void send(String message)
    {
        try
        {
            TextMessage textMessage = session.createTextMessage(message);

            producer.send(textMessage);
        }
        catch (JMSException e)
        {
            JexxaLogger.getLogger(MessageSender.class).error(e.getMessage());
        }
    }

    public void close()
    {
        try
        {
            if (session != null)
            {
                session.close();
            }
        } catch (JMSException e)
        {
            JexxaLogger.getLogger(MessageSender.class).error(e.getMessage());
        }

        try
        {
            if (connection != null)
            {
                connection.close();
            }
        } catch (JMSException e)
        {
            JexxaLogger.getLogger(MessageSender.class).error(e.getMessage());
        }
    }

}