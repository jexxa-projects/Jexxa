package io.jexxa.drivingadapter.messaging;


import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.utils.JexxaBanner;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.api.function.ThrowingConsumer;
import io.jexxa.utils.properties.JexxaJMSProperties;
import io.jexxa.utils.properties.Secret;
import org.apache.commons.lang3.Validate;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;
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
    private Connection connection;
    private final List<Session> sessionList = new ArrayList<>();
    private final List<MessageConsumer> consumerList = new ArrayList<>();
    private final List<Object> registeredListener = new ArrayList<>();
    private final List<JMSConfiguration> jmsConfigurationList = new ArrayList<>();
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

        JexxaBanner.addAccessBanner(this::bannerInformation);
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
        if (simulateJMS) {
            return;
        }

        try {
            var messageListener = (MessageListener) (object);
            var jmsConfiguration = getConfiguration(object);

            var session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            sessionList.add(session);

            Destination destination = createDestination(session, jmsConfiguration);
            MessageConsumer consumer = createMessageConsumer(session, destination, jmsConfiguration);

            var invocationHandler = InvocationManager.getInvocationHandler(messageListener);
            consumer.setMessageListener(message -> invocationHandler.invoke(messageListener, messageListener::onMessage, message));

            consumerList.add(consumer);
            registeredListener.add(object);
            jmsConfigurationList.add(jmsConfiguration);
        } catch (JMSException e) {
            throw new IllegalStateException(
                    "Registration of of Driving Adapter " + object.getClass().getName() + " failed. Please check the JMSConfiguration.\n" +
                            " Error message from JMS subsystem: " + e.getMessage()
                    , e
            );
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
        String selector = null;
        if (!jmsConfiguration.selector().isEmpty())
        {
            selector = jmsConfiguration.selector();
        }

        if ( jmsConfiguration.sharedSubscriptionName().isEmpty()
                && jmsConfiguration.durable().equals(JMSConfiguration.DurableType.NON_DURABLE))
        {
            return session.createConsumer(destination, selector);
        }
        //From here we must have a Topic
        if (jmsConfiguration.messagingType().equals(JMSConfiguration.MessagingType.QUEUE))
        {
            throw new IllegalArgumentException("Invalid JMSConfiguration: A shared jms connection is defined which requires a MessagingType QUEUE");
        }
        //Not Shared and durable
        if ( jmsConfiguration.sharedSubscriptionName().isEmpty()
                && jmsConfiguration.durable().equals(JMSConfiguration.DurableType.DURABLE))
        {
            return session.createDurableConsumer((Topic)destination, jmsConfiguration.sharedSubscriptionName(), selector, false);
        }

        // Shared and durable
        if ( !jmsConfiguration.sharedSubscriptionName().isEmpty()
                && jmsConfiguration.durable().equals(JMSConfiguration.DurableType.DURABLE))
        {
            return session.createSharedDurableConsumer((Topic)destination, jmsConfiguration.sharedSubscriptionName(), selector);
        }

        // Shared and non-durable
        if ( !jmsConfiguration.sharedSubscriptionName().isEmpty()
                && jmsConfiguration.durable().equals(JMSConfiguration.DurableType.NON_DURABLE))
        {
            return session.createSharedConsumer((Topic)destination, jmsConfiguration.sharedSubscriptionName(), selector);
        }
        throw new IllegalArgumentException("Invalid JMSConfiguration for " + jmsConfiguration.destination());
    }

    @Override
    public void close()
    {
        consumerList.forEach(consumer -> Optional.ofNullable(consumer).ifPresent(ThrowingConsumer.exceptionLogger(MessageConsumer::close)));
        sessionList.forEach(ThrowingConsumer.exceptionLogger(Session::close));
        Optional.ofNullable(connection).ifPresent(ThrowingConsumer.exceptionLogger(Connection::close));

        registeredListener.clear();
        consumerList.clear();
        jmsConfigurationList.clear();
        sessionList.clear();
    }


    public static Connection createConnection(Properties properties)
    {
        var username = new Secret(properties, JexxaJMSProperties.JNDI_USER_KEY, JexxaJMSProperties.JNDI_USER_FILE);
        var password = new Secret(properties, JexxaJMSProperties.JNDI_PASSWORD_KEY, JexxaJMSProperties.JNDI_PASSWORD_FILE);

        try
        {
            var initialContext = new InitialContext(properties);
            var connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
            return connectionFactory.createConnection(username.getSecret(), password.getSecret());
        }
        catch (NamingException e)
        {
            throw new IllegalStateException("No ConnectionFactory available via : " + properties.get(JexxaJMSProperties.JNDI_PROVIDER_URL_KEY), e);
        }
        catch (JMSException e)
        {
            throw new IllegalStateException("Can not connect to " + properties.get(JexxaJMSProperties.JNDI_PROVIDER_URL_KEY), e);
        }
    }

    public Connection getConnection()
    {
        return connection;
    }

    public List<MessageConsumer> getConsumerList()
    {
        return consumerList;
    }

    private JMSConfiguration getConfiguration(Object object)
    {
        //Find method annotated with JMSConfiguration
        var jmsConfigrationMethod = Arrays.stream(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(JMSConfiguration.class) ||
                        (JMSConfiguration.class.isAssignableFrom( method.getReturnType()) && method.getParameterCount() == 0))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Given object %s does not provide a %s for any public method!"
                                , object.getClass().getSimpleName()
                                , JMSConfiguration.class.getSimpleName())));


        if (jmsConfigrationMethod.isAnnotationPresent(JMSConfiguration.class))
        {
            //Return JMSConfiguration from annotation
            return jmsConfigrationMethod.getAnnotation(JMSConfiguration.class);
        } else {
            //Else return JMSConfiguration by invoking a method
            try {
                return (JMSConfiguration) jmsConfigrationMethod.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e)
            {
                throw new IllegalArgumentException(
                        String.format("Given object %s does not provide a %s for any public method!"
                                , object.getClass().getSimpleName()
                                , JMSConfiguration.class.getSimpleName()), e);
            }
        }
    }

    private void initConnection() throws JMSException
    {
        if (simulateJMS)
        {
            JexxaLogger.getLogger(JMSAdapter.class).warn("JMSAdapter is running in simulation mode -> No messages will be received");
            return;
        }

        connection = createConnection(properties);
        if (properties.containsKey(JexxaJMSProperties.JNDI_CLIENT_ID) &&
                properties.getProperty(JexxaJMSProperties.JNDI_CLIENT_ID) != null &&
                !properties.getProperty(JexxaJMSProperties.JNDI_CLIENT_ID).isEmpty() )
        {
            connection.setClientID(properties.getProperty(JexxaJMSProperties.JNDI_CLIENT_ID));
        }

        // NOTE: The exception handler is created after the session is successfully created
        connection.setExceptionListener(exception -> {
            JexxaLogger.getLogger(JMSAdapter.class).error(exception.getMessage());
            jmsConnectionExceptionHandler.stopFailover();
            jmsConnectionExceptionHandler.startFailover();
        });
    }

    private void validateProperties(Properties properties)
    {
        if (simulateJMS)
        {
            return;
        }

        Validate.isTrue(properties.containsKey(JexxaJMSProperties.JNDI_PROVIDER_URL_KEY), "Property + " + JexxaJMSProperties.JNDI_PROVIDER_URL_KEY + " is missing ");
        Validate.isTrue(properties.containsKey(JexxaJMSProperties.JNDI_FACTORY_KEY), "Property + " + JexxaJMSProperties.JNDI_FACTORY_KEY + " is missing ");
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
    public void bannerInformation(Properties properties)
    {
        var topics = Arrays.toString(
                jmsConfigurationList.stream()
                .filter( element -> element.messagingType().equals(JMSConfiguration.MessagingType.TOPIC))
                .map(JMSConfiguration::destination).toArray()
        );

        var queues = Arrays.toString( jmsConfigurationList.stream()
                .filter( element -> element.messagingType().equals(JMSConfiguration.MessagingType.QUEUE))
                .map(JMSConfiguration::destination).toArray()
        );

        JexxaLogger.getLogger(JexxaBanner.class).info("JMS Listening on  : {}", properties.getProperty(JexxaJMSProperties.JNDI_PROVIDER_URL_KEY));
        JexxaLogger.getLogger(JexxaBanner.class).info("   * JMS-Topics   : {}", topics);
        JexxaLogger.getLogger(JexxaBanner.class).info("   * JMS-Queues   : {}", queues);
    }

}
