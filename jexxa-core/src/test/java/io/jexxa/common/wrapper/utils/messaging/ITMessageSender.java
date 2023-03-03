package io.jexxa.common.wrapper.utils.messaging;

import io.jexxa.common.function.ThrowingConsumer;
import io.jexxa.drivingadapter.messaging.JMSConfiguration;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.Optional;
import java.util.Properties;

import static io.jexxa.common.wrapper.jms.JMSConnection.createConnection;
import static org.slf4j.LoggerFactory.getLogger;

public class ITMessageSender implements AutoCloseable
{
    private final Connection connection;
    private final Session session;
    private final MessageProducer producer;

    public ITMessageSender(Properties properties, String messageDestination, JMSConfiguration.MessagingType messagingType)
    {
        try
        {
            this.connection = createConnection(properties);
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
            getLogger(getClass()).error(e.getMessage());
        }
    }

    public void close()
    {
        Optional.ofNullable(session)
                .ifPresent(ThrowingConsumer.exceptionLogger(Session::close, getLogger(ITMessageSender.class)));

        Optional.ofNullable(connection)
                .ifPresent(ThrowingConsumer.exceptionLogger(Connection::close, getLogger(ITMessageSender.class)));
    }

}