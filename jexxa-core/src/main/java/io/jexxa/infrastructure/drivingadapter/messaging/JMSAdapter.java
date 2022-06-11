package io.jexxa.infrastructure.drivingadapter.messaging;


import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;
import io.jexxa.utils.properties.Secret;
import org.apache.commons.lang3.Validate;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.jexxa.utils.properties.JexxaJMSProperties.JEXXA_JMS_SIMULATE;

public class JMSAdapter implements AutoCloseable, IDrivingAdapter
{
    public static final String JNDI_PROVIDER_URL_KEY = "java.naming.provider.url";
    public static final String JNDI_USER_KEY = "java.naming.user";
    public static final String JNDI_PASSWORD_KEY = "java.naming.password";
    public static final String JNDI_FACTORY_KEY = "java.naming.factory.initial";
    public static final String JNDI_PASSWORD_FILE = "java.naming.file.password";
    public static final String JNDI_USER_FILE = "java.naming.file.user";

    public static final String DEFAULT_JNDI_PROVIDER_URL = "tcp://localhost:61616";
    public static final String DEFAULT_JNDI_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";

    private Connection connection;
    private Session session;
    private final List<MessageConsumer> consumerList = new ArrayList<>();
    private final List<Object> registeredListener = new ArrayList<>();
    private final JMSConnectionExceptionHandler jmsConnectionExceptionHandler;

    private final boolean simulateJMS;
    private final Properties properties;

    public JMSAdapter(final Properties properties)
    {
        simulateJMS = properties.containsKey(JEXXA_JMS_SIMULATE);
        Objects.requireNonNull(properties);
        validateProperties(properties);

        this.jmsConnectionExceptionHandler = new JMSConnectionExceptionHandler(this, registeredListener);
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
            if (!simulateJMS) {
                jmsConnectionExceptionHandler.setListener(registeredListener);
                connection.start();
            }
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
    }

    @SuppressWarnings("java:S2095") // We must not close the connection
    @Override
    public void register(Object object)
    {
        if (!simulateJMS) {
            try {
                var messageListener = (MessageListener) (object);
                var jmsConfiguration = getConfiguration(object);

                Destination destination = createDestination(session, jmsConfiguration);
                MessageConsumer consumer = createMessageConsumer(session, destination, jmsConfiguration);

                var invocationHandler = InvocationManager.getInvocationHandler(messageListener);
                consumer.setMessageListener(message -> invocationHandler.invoke(messageListener, messageListener::onMessage, message));

                consumerList.add(consumer);
                registeredListener.add(object);
            } catch (JMSException e) {
                throw new IllegalStateException(
                        "Registration of of Driving Adapter " + object.getClass().getName() + " failed. Please check the JMSConfiguration.\n" +
                                " Error message from JMS subsystem: " + e.getMessage()
                        , e
                );
            }
        }
    }


    private Destination createDestination(Session session, JMSConfiguration jmsConfiguration) throws JMSException {
        if (jmsConfiguration.messagingType() == JMSConfiguration.MessagingType.TOPIC)
        {
            return session.createTopic(jmsConfiguration.destination());
        }
        else
        {
            return session.createQueue(jmsConfiguration.destination());
        }
    }

    private MessageConsumer createMessageConsumer(Session session, Destination destination, JMSConfiguration jmsConfiguration) throws JMSException {
        if (jmsConfiguration.selector().isEmpty())
        {
            return session.createConsumer(destination);
        }
        else
        {
            return session.createConsumer(destination, jmsConfiguration.selector());
        }
    }


    @Override
    public void close()
    {
        consumerList.forEach(consumer -> Optional.ofNullable(consumer).ifPresent(ThrowingConsumer.exceptionLogger(MessageConsumer::close)));
        Optional.ofNullable(session).ifPresent(ThrowingConsumer.exceptionLogger(Session::close));
        Optional.ofNullable(connection).ifPresent(ThrowingConsumer.exceptionLogger(Connection::close));

        registeredListener.clear();
        consumerList.clear();
    }


    public static Connection createConnection(Properties properties)
    {
        var username = new Secret(properties, JNDI_USER_KEY, JNDI_USER_FILE);
        var password = new Secret(properties, JNDI_PASSWORD_KEY, JNDI_PASSWORD_FILE);

        try
        {
            var initialContext = new InitialContext(properties);
            var connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            return connectionFactory.createConnection(username.getSecret(), password.getSecret());
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

    List<MessageConsumer> getConsumerList()
    {
        return consumerList;
    }

    private JMSConfiguration getConfiguration(Object object)
    {
        return Arrays.stream(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(JMSConfiguration.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Given object %s does not provide a %s for any public method!"
                                , object.getClass().getSimpleName()
                                , JMSConfiguration.class.getSimpleName())))
                .getDeclaredAnnotation(JMSConfiguration.class);
    }

    private void initConnection() throws JMSException
    {
        if (!simulateJMS) {
            connection = createConnection(properties);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // NOTE: The exception handler is created after the session is successfully created
            connection.setExceptionListener(exception -> {
                JexxaLogger.getLogger(JMSAdapter.class).error(exception.getMessage());
                jmsConnectionExceptionHandler.stopFailover();
                jmsConnectionExceptionHandler.startFailover();
            });
        } else {
            JexxaLogger.getLogger(JMSAdapter.class).warn("JMSAdapter is running in simulation mode -> No messages will be received");
        }

    }

    private void validateProperties(Properties properties)
    {
        if (!simulateJMS) {
            Validate.isTrue(properties.containsKey(JNDI_PROVIDER_URL_KEY), "Property + " + JNDI_PROVIDER_URL_KEY + " is missing ");
            Validate.isTrue(properties.containsKey(JNDI_FACTORY_KEY), "Property + " + JNDI_FACTORY_KEY + " is missing ");
        }
    }

    /**
     * Handles Exceptions in a JMS connection by starting a new connection and re-register all JMSListener
     */
    private static class JMSConnectionExceptionHandler
    {
        private final JMSAdapter jmsAdapter;
        private List<Object> listener;
        private ScheduledExecutorService executorService;

        JMSConnectionExceptionHandler(JMSAdapter jmsAdapter, List<Object> listener)
        {
            this.jmsAdapter = jmsAdapter;
            this.listener = listener;
            executorService = Executors.newSingleThreadScheduledExecutor();
        }

        public void setListener ( List<Object> listener)
        {
            this.listener = new ArrayList<>(listener);
        }

        public void stopFailover()
        {
            //NOTE: following code is taken from JavaDoc of ExecutorService
            executorService.shutdown();
            try
            {
                // Wait a while for existing tasks to terminate
                if ( !executorService.awaitTermination(500, TimeUnit.MILLISECONDS) )
                {
                    executorService.shutdownNow();
                    // Wait a while for tasks to respond to being cancelled
                    if ( !executorService.awaitTermination(500, TimeUnit.MILLISECONDS))
                    {
                        JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).error("stopFailover ExecutorService did not terminate.");
                    }
                }
            }
            catch (InterruptedException e)
            {
                 // (Re-)Cancel if current thread also interrupted
                executorService.shutdownNow();
                 // Preserve interrupt status
                Thread.currentThread().interrupt();
            }
        }

        public void startFailover()
        {
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(this::restartSubscription, 0, 3000, TimeUnit.MILLISECONDS);
        }

        private synchronized void restartSubscription()
        {
            try
            {
                JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).warn("Try to restart JMS message listener");

                jmsAdapter.close();
                jmsAdapter.initConnection();
                listener.forEach(jmsAdapter::register);
                jmsAdapter.getConnection().start();

                executorService.shutdown();  //Shutdown service if restart was successful

                JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).warn("Listener successfully restarted with {} consumer", listener.size());
                listener.forEach( element ->
                        JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).warn("Restarted Listener {}", element.getClass().getSimpleName())
                );
            }
            catch (JMSException | IllegalStateException e)
            {
                JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).error("Failed to restart JMS Listener");
                JexxaLogger.getLogger(JMSConnectionExceptionHandler.class).error(e.getMessage());
            }
        }
    }

}
