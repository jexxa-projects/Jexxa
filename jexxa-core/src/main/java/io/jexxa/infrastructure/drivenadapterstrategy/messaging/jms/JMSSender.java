package io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@SuppressWarnings({"unused", "java:S1133"})
public class JMSSender extends MessageSender implements AutoCloseable
{
    public static final String JNDI_PROVIDER_URL_KEY = "java.naming.provider.url";
    public static final String JNDI_USER_KEY = "java.naming.user";
    public static final String JNDI_PASSWORD_KEY = "java.naming.password";
    public static final String JNDI_PASSWORD_FILE = "java.naming.password_file";
    public static final String JNDI_FACTORY_KEY = "java.naming.factory.initial";


    public static final String DEFAULT_JNDI_PROVIDER_URL = "tcp://localhost:61616";
    public static final String DEFAULT_JNDI_USER = "admin";
    /**
     * @deprecated to be removed in future releases to ensure that no default PWs are used
     */
    @Deprecated(forRemoval = true)
    public static final String DEFAULT_JNDI_PASSWORD = "admin";
    public static final String DEFAULT_JNDI_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private final Properties properties;
    private Connection connection;
    private Session session;

    public JMSSender(Properties properties)
    {
        this.properties = properties;
        Objects.requireNonNull(getConnection()); //Try to create a connection to ensure fail fast
    }

    protected void sendToTopic(String message, String topicName, Properties messageProperties, MessageType messageType)
    {
        try
        {
            var destination = getSession().createTopic(topicName);
            try (var producer = getSession().createProducer(destination) )
            {
                sendJMSMessage(message, producer, messageProperties, messageType);
            }
        }
        catch (JMSException e)
        {
            close();
            throw new IllegalStateException("Could not send message", e);
        }
    }

    protected void sendToQueue(String message, String queueName, Properties messageProperties, MessageType messageType)
    {
        try
        {
            var destination = getSession().createQueue(queueName);
            try (var producer = getSession().createProducer(destination) )
            {
                sendJMSMessage(message, producer, messageProperties, messageType);
            }
        }
        catch (JMSException e)
        {
            close();
            throw new IllegalStateException("Could not send message ", e);
        }
    }

    private void sendJMSMessage(String message, MessageProducer messageProducer, Properties messageProperties, MessageType messageType) throws JMSException
    {
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        var jmsMessage = createMessage(messageType, message);

        if (messageProperties != null)
        {
            for (Map.Entry<Object, Object> entry : messageProperties.entrySet())
            {
                jmsMessage.setStringProperty(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        messageProducer.send(jmsMessage);
    }

    private Message createMessage(MessageType messageType, String message) throws JMSException
    {
        if (messageType == MessageType.BYTE_MESSAGE)
        {
            var bytesMessage = getSession().createBytesMessage();
            bytesMessage.writeUTF(message);
            return bytesMessage;
        }
        return getSession().createTextMessage(message);
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
            var initialContext = new InitialContext(properties);
            var connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            var connection = connectionFactory.createConnection(properties.getProperty(JNDI_USER_KEY), getPassword(properties));

            //Register an exception listener that closes the connection as soon as the error occurs. This approach ensure that we recreate a connection
            // as soon as next message must be sent, and we can handle a temporary error in between sending two messages. If the error still exist, the
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

    private static String getPassword(Properties properties)
    {
        try {
            if (properties.containsKey(JNDI_PASSWORD_FILE)) {
                return Files
                        .readAllLines(Path.of(properties.getProperty(JNDI_PASSWORD_FILE)))
                        .get(0);
            }

            return properties.getProperty(JNDI_PASSWORD_KEY);
        } catch (IOException e)
        {
            throw new IllegalArgumentException(e);
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
