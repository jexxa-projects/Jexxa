package io.ddd.jexxa.infrastructure.drivingadapter.messaging;


import static io.ddd.jexxa.utils.ThrowingConsumer.exceptionLogger;

import java.util.Optional;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.jexxa.utils.JexxaLogger;


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

    private Connection connection = null;
    private Session session = null;
    private MessageConsumer consumer = null;

    private final Properties properties;

    public JMSAdapter(final Properties properties)
    {
        this.properties = properties;
    }



    public void start()
    {
        try
        {
            connection.start();
        }
        catch (JMSException e)
        {
            // TODO: Should we us our exception handler?!
            throw new IllegalStateException("Driving Adapter could not start receiving messages", e);
        }

    }

    @Override
    public void stop()
    {
        close();
    }

    @Override
    public void register(Object object)
    {
        try
        {
            var messageListener = (MessageListener) (object);

            connection = create(properties);

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic(messageListener.getClass().getSimpleName());

            //TODO handle MessageSelector
            consumer = session.createConsumer(destination);

            consumer.setMessageListener(messageListener);
        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Driving Adapter could not connect to JMS-System", e);
        }

    }


    @Override
    public void close()
    {
        Optional.ofNullable(consumer).ifPresent(exceptionLogger(MessageConsumer::close));
        Optional.ofNullable(session).ifPresent(exceptionLogger(Session::close));
        Optional.ofNullable(connection).ifPresent(exceptionLogger(Connection::close));
    }

    
    private Connection create(Properties jndiProperties)
    {
        try 
        {
            final InitialContext initialContext = new InitialContext(jndiProperties);
            final ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            var connection = connectionFactory.createConnection(jndiProperties.getProperty(JNDI_USER_KEY), jndiProperties.getProperty(JNDI_PASSWORD_KEY));
            connection.setExceptionListener(exception -> JexxaLogger.getLogger(JMSAdapter.class).error(exception.getMessage()));
            return connection;
        }
        catch (NamingException e)
        {
            throw new IllegalStateException("No ConnectionFactory available via : " + jndiProperties.get(JNDI_PROVIDER_URL_KEY), e);
        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Can not connect to " + jndiProperties.get(JNDI_PROVIDER_URL_KEY), e);
        }
    }
}
