package io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms;

import java.util.Map;
import java.util.Optional;
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
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.ThrowingConsumer;
import org.apache.commons.lang3.Validate;

@SuppressWarnings({"unused", "java:S1133"})
public class JMSSender extends MessageSender implements AutoCloseable
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
    private Connection connection;
    private Session session;

    public JMSSender(Properties properties)
    {
        this.properties = properties;
        Validate.notNull(getConnection()); //Try create a connection to ensure fail fast
    }
    
    /**
     * @deprecated Please use {@link #send(Object)}
     * @param message to be send
     * @param topicName name of the topic
     * @param <T> Type of the message
     */
    @Deprecated(forRemoval = true)
    public <T> void sendToTopic(T message, String topicName)
    {
        sendToTopic(message, topicName, null);
    }

    /**
     * @deprecated Please use {@link #send(Object)}
     * @param message to be send
     * @param topicName name of the topic
     * @param messageProperties properties added to message header 
     * @param <T> Type of the message
     */
    @Deprecated(forRemoval = true)
    public <T> void sendToTopic(T message, String topicName, Properties messageProperties)
    {
        var gson = new Gson();
        sendMessageToTopic(gson.toJson(message), topicName, messageProperties);
    }

    protected void sendMessageToTopic(String message, String topicName, Properties messageProperties)
    {
        try
        {
            var destination = getSession().createTopic(topicName);
            try (var producer = getSession().createProducer(destination) )
            {
                sendTextMessage(message, producer, messageProperties);
            }
        }
        catch (JMSException e)
        {
            close();
            throw new IllegalStateException("Could not send message", e);                         
        }
    }


    /**
     * @deprecated Please use {@link #send(Object)}
     * @param message to be send
     * @param queue name of the queue
     * @param <T> Type of the message
     */
    @Deprecated(forRemoval = true)
    public <T> void sendToQueue(T message, String queue)
    {
        sendToQueue(message, queue, null);
    }

    /**
     * @deprecated Please use {@link #send(Object)}
     * @param message to be send
     * @param queueName name of the queue
     * @param messageProperties properties added to message header
     * @param <T> Type of the message
     */
    @Deprecated(forRemoval = true)
    public <T> void sendToQueue(T message, String queueName, Properties messageProperties)
    {
        var gson = new Gson();
        sendMessageToQueue(gson.toJson(message), queueName, messageProperties);
    }

    protected void sendMessageToQueue(String message, String queueName, Properties messageProperties)
    {
        try
        {
            var destination = getSession().createQueue(queueName);
            try (var producer = getSession().createProducer(destination) )
            {
                sendTextMessage(message, producer, messageProperties);
            }
        }
        catch (JMSException e)
        {
            close();
            throw new IllegalStateException("Could not send message ", e);
        }
    }

    private void sendTextMessage(String message, MessageProducer messageProducer, Properties messageProperties) throws JMSException
    {
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        var textMessage = getSession().createTextMessage(message);

        if (messageProperties != null)
        {
            for (Map.Entry<Object, Object> entry : messageProperties.entrySet())
            {
                textMessage.setStringProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        messageProducer.send(textMessage);
    }

    Session getSession() throws JMSException
    {
        if (this.session == null)
        {
            this.session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
        }

        return this.session;
    }


    final Connection getConnection()
    {
        if (connection == null)
        {
            connection = createConnection(properties, this);
        }
        
        return connection;
    }

    @SuppressWarnings("java:S2095") 
    private static Connection createConnection(Properties properties, JMSSender jmsSender)
    {
        try
        {
            final InitialContext initialContext = new InitialContext(properties);
            final ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            Connection connection = connectionFactory.createConnection(properties.getProperty(JNDI_USER_KEY), properties.getProperty(JNDI_PASSWORD_KEY));

            //Register an exception listener that closes the connection as soon as the error occurs. This approach ensure that we recreate a connection
            // as soon as next message must be send and we cab handle a temporarily error in between sending two messages. If the error still exist, the
            // application will get a RuntimeError 
            connection.setExceptionListener( exception -> {
                JexxaLogger.getLogger(JMSSender.class).error(exception.getMessage());
                jmsSender.close();
            });

            return connection;
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

    @Override
    public void close()
    {
        Optional.ofNullable(session)
                .ifPresent(ThrowingConsumer.exceptionLogger(Session::close));

        Optional.ofNullable(connection)
                .ifPresent(ThrowingConsumer.exceptionLogger(Connection::close));

        session = null;
        connection = null;
    }
}
