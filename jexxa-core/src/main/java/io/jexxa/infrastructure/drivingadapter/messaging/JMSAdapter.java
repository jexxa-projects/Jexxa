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
import javax.jms.Message;
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
    public static final String DEFAULT_JNDI_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private Connection connection;
    private Session session;
    private final List<MessageConsumer> consumerList = new ArrayList<>();
    private final List<Object> registeredListener = new ArrayList<>();
    private JMSRestart jmsRestart;

    private final Properties properties;

    public JMSAdapter(final Properties properties)
    {
        validateProperties(properties);

        this.properties = properties;

        try
        {
            initConnection();
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
        if ( jmsRestart != null ) {
            jmsRestart.stop();
        }
        close();
        registeredListener.clear();
    }

    @SuppressWarnings("java:S2095") // We must not close the connection
    @Override
    public void register(Object object)
    {
        try
        {
            var messageListener = (MessageListener) (object);

            JMSConfiguration jmsConfiguration = getConfiguration(object);

            Destination destination;
            if (jmsConfiguration.messagingType() == JMSConfiguration.MessagingType.TOPIC)
            {
                destination = session.createTopic(jmsConfiguration.destination());
            }
            else
            {
                destination = session.createQueue(jmsConfiguration.destination());
            }

            MessageConsumer consumer;
            if (jmsConfiguration.selector().isEmpty())
            {
                consumer = session.createConsumer(destination);
            }
            else
            {
                consumer = session.createConsumer(destination, jmsConfiguration.selector());
            }
            consumer.setMessageListener(new SynchronizedMessageListener(messageListener));
            consumerList.add(consumer);
            registeredListener.add(object);
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
    protected Connection createConnection()
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

    protected Connection getConnection()
    {
        return connection;
    }

    private JMSConfiguration getConfiguration(Object object)
    {
        return Arrays.stream(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(JMSConfiguration.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Given object does not provide a " + JMSConfiguration.class.getSimpleName()))
                .getDeclaredAnnotation(JMSConfiguration.class);
    }

    private void initConnection() throws JMSException
    {
        connection = createConnection();
        connection.setExceptionListener(exception -> {
                    JexxaLogger.getLogger(JMSAdapter.class).error(exception.getMessage());

                    if (jmsRestart != null) { // Stop any previous restarter if available
                        jmsRestart.stop();
                    }

                    jmsRestart = new JMSRestart(this, new ArrayList<>(registeredListener));
                    jmsRestart.start();
        });
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void validateProperties(Properties properties)
    {
        Validate.isTrue(properties.containsKey(JNDI_PROVIDER_URL_KEY), "Property + " + JNDI_PROVIDER_URL_KEY + " is missing ");
        Validate.isTrue(properties.containsKey(JNDI_FACTORY_KEY), "Property + " + JNDI_FACTORY_KEY + " is missing ");
    }

    static class SynchronizedMessageListener implements MessageListener
    {
        private final MessageListener jmsListener;

        SynchronizedMessageListener(MessageListener jmsListener)
        {
            Validate.notNull(jmsListener);
            this.jmsListener = jmsListener;
        }


        @Override
        public void onMessage(Message message)
        {
            synchronized (IDrivingAdapter.acquireLock().getSynchronizationObject())
            {
                jmsListener.onMessage(message);
            }
        }
    }

    static class JMSRestart
    {
        final JMSAdapter jmsAdapter;
        final List<Object> listener;
        boolean isRunning = false;
        Thread thread;

        JMSRestart(JMSAdapter jmsAdapter, List<Object> listener)
        {
            this.jmsAdapter = jmsAdapter;
            this.listener = listener;
        }

        public void stop()
        {
            isRunning = false;
            try
            {
                thread.join();
            }
            catch (InterruptedException e)
            {
                JexxaLogger.getLogger(JMSRestart.class).error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        public void start()
        {
            thread = new Thread(() -> {
                isRunning = true;
                while (isRunning)
                {
                    try
                    {
                        JexxaLogger.getLogger(JMSRestart.class).warn("Try to restart JMS message listener");

                        jmsAdapter.close();
                        jmsAdapter.initConnection();
                        listener.forEach(jmsAdapter::register);
                        jmsAdapter.start();
                        
                        JexxaLogger.getLogger(JMSRestart.class).warn("Listener successfully restarted");
                        return;
                    }
                    catch (JMSException | IllegalStateException e)
                    {
                        JexxaLogger.getLogger(JMSRestart.class).error("Failed to restart JMS Listener");
                        JexxaLogger.getLogger(JMSRestart.class).error(e.getMessage());
                    }

                    try
                    {
                        //noinspection BusyWait
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        JexxaLogger.getLogger(JMSRestart.class).error(e.getMessage());
                        Thread.currentThread().interrupt();
                        return;
                    }

                }
            });
            thread.start();
        }
    }
}
