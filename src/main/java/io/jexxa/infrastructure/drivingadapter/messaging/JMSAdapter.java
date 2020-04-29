package io.jexxa.infrastructure.drivingadapter.messaging;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.ThrowingConsumer;
import org.apache.commons.lang.Validate;


public class JMSAdapter implements AutoCloseable, IDrivingAdapter
{
    public static final String JNDI_PROVIDER_URL_KEY = "java.naming.provider.url";
    public static final String JNDI_USER_KEY = "java.naming.user";
    public static final String JNDI_PASSWORD_KEY = "java.naming.password";
    public static final String JNDI_FACTORY_KEY = "java.naming.factory.initial";


    public static final String DEFAULT_JNDI_PROVIDER_URL = "tcp://localhost:61616";
    public static final String DEFAULT_JNDI_USER = "admin";
    public static final String DEFAULT_JNDI_PASSWORD = "admin";
    public static final String DEFAULT_JNDI_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private final Connection connection;
    private final Session session;
    private final List<MessageConsumer> consumerList = new ArrayList<>();

    private final Properties properties;

    public JMSAdapter(final Properties properties)
    {
        validateProperties(properties);

        this.properties = properties;

        try
        {
            connection = createConnection();
            connection.setExceptionListener(exception -> JexxaLogger.getLogger(JMSAdapter.class).error(exception.getMessage()));
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        }
        catch (JMSException e)
        {
            throw new IllegalArgumentException(e);
        }
    }



    public void start()
    {
        try
        {
            connection.start();
        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Driving Adapter could not start receiving messages", e);
        }

    }

    @Override
    public void stop()
    {
        close();
    }

    @SuppressWarnings("java:S2095") // We must not close the connection
    @Override
    public void register(Object object)
    {
        try
        {
            var messageListener = (MessageListener) (object);

            JMSListener jmsListener = getJMSListener(object);

            Destination destination;
            if (jmsListener.messagingType() == JMSListener.MessagingType.TOPIC)
            {
                destination = session.createTopic(jmsListener.destination());
            }
            else
            {
               destination = session.createQueue(jmsListener.destination());
            }

            MessageConsumer consumer;
            if (jmsListener.selector().isEmpty())
            {
                consumer = session.createConsumer(destination);
            }
            else
            {
                consumer = session.createConsumer(destination, jmsListener.selector());
            }
            consumer.setMessageListener(messageListener);
            consumerList.add(consumer);

        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Driving Adapter could not connect to JMS-System", e);
        }

    }


    @Override
    public void close()
    {
        consumerList.forEach(consumer -> Optional.ofNullable(consumer).ifPresent(ThrowingConsumer.exceptionLogger(MessageConsumer::close)));
        Optional.ofNullable(session).ifPresent(ThrowingConsumer.exceptionLogger(Session::close));
        Optional.ofNullable(connection).ifPresent(ThrowingConsumer.exceptionLogger(Connection::close));
    }

    
    @SuppressWarnings("DuplicatedCode")
    Connection createConnection()
    {
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

    private JMSListener getJMSListener(Object object)
    {
        return Arrays.stream(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(JMSListener.class))
                .findFirst()
                .orElseThrow().getDeclaredAnnotation(JMSListener.class);
    }

    private void validateProperties(Properties properties)
    {
        Validate.isTrue(properties.containsKey(JNDI_PROVIDER_URL_KEY), "Property + " + JNDI_PROVIDER_URL_KEY + " is missing ");
        Validate.isTrue(properties.containsKey(JNDI_FACTORY_KEY), "Property + " + JNDI_FACTORY_KEY + " is missing ");
    }
}
