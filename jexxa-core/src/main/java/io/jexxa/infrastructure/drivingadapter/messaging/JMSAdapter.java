package io.jexxa.infrastructure.drivingadapter.messaging;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import org.apache.commons.lang3.Validate;


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
    private JMSConnectionExceptionHandler jmsConnectionExceptionHandler;

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
        Optional.ofNullable( jmsConnectionExceptionHandler )
                .ifPresent(JMSConnectionExceptionHandler::stopFailover);
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
    public static Connection createConnection(Properties properties)
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
        connection = createConnection(properties);
        connection.setExceptionListener(exception -> {
                    JexxaLogger.getLogger(JMSAdapter.class).error(exception.getMessage());

                    if (jmsConnectionExceptionHandler != null) { // Stop any previous exception handler if available
                        jmsConnectionExceptionHandler.stopFailover();
                    }

                    jmsConnectionExceptionHandler = new JMSConnectionExceptionHandler(this, new ArrayList<>(registeredListener));
                    jmsConnectionExceptionHandler.startFailover();
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

    /**
     * Handles Exceptions in a JMS connection by starting a new connection and re-register all JMSListener   
     */
    private static class JMSConnectionExceptionHandler
    {
        private final JMSAdapter jmsAdapter;
        private final List<Object> listener;
        private final ScheduledExecutorService executorService;

        JMSConnectionExceptionHandler(JMSAdapter jmsAdapter, List<Object> listener)
        {
            this.jmsAdapter = jmsAdapter;
            this.listener = listener;
            executorService = Executors.newSingleThreadScheduledExecutor();
        }

        public void stopFailover()
        {
            try
            {
                executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }

        public void startFailover()
        {
            executorService.scheduleAtFixedRate(this::restartSubscription, 0, 500, TimeUnit.MILLISECONDS);
        }

        private void restartSubscription()    {
            try
            {
                JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).warn("Try to restart JMS message listener");

                jmsAdapter.close();
                jmsAdapter.initConnection();
                listener.forEach(jmsAdapter::register);
                jmsAdapter.start();

                executorService.shutdown();  //Shutdown service if restart was successful
                JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).warn("Listener successfully restarted");
            }
            catch (JMSException | IllegalStateException e)
            {
                JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).error("Failed to restart JMS Listener");
                JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).error(e.getMessage());
            }

        }

    }
}
