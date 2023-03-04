package io.jexxa.drivingadapter.messaging;


import io.jexxa.TestConstants;
import io.jexxa.common.wrapper.jms.JMSProperties;
import io.jexxa.testapplication.JexxaTestApplication;
import io.jexxa.testapplication.domain.model.JexxaEntity;
import io.jexxa.core.JexxaMain;
import io.jexxa.drivingadapter.messaging.listener.ConfigurableListener;
import io.jexxa.drivingadapter.messaging.listener.ITMessageSender;
import io.jexxa.drivingadapter.messaging.listener.QueueListener;
import io.jexxa.drivingadapter.messaging.listener.SharedConnectionListener;
import io.jexxa.drivingadapter.messaging.listener.TopicListener;
import io.jexxa.infrastructure.persistence.repository.jdbc.JDBCKeyValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.jexxa.drivingadapter.messaging.listener.TopicListener.TOPIC_DESTINATION;
import static io.jexxa.common.JexxaCoreProperties.JEXXA_APPLICATION_PROPERTIES;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@SuppressWarnings("resource")
@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JMSAdapterIT
{
    private static final String DEFAULT_JNDI_PROVIDER_URL = "tcp://localhost:61616";
    private static final String DEFAULT_JNDI_FACTORY = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
    private static final String MESSAGE = "Hello World";

    private Properties properties;

    @BeforeEach
    void initTests() throws IOException
    {
        //Arrange
        properties = new Properties();
        properties.load(getClass().getResourceAsStream(JEXXA_APPLICATION_PROPERTIES));
    }

    @Test
    void startJMSAdapterTopic()
    {
        //Arrange
        var topicListener = new TopicListener();

        try (  var objectUnderTest = new JMSAdapter(properties) )
        {
            objectUnderTest.register(topicListener);

            ITMessageSender topicSender = new ITMessageSender(properties, TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);
            //Act
            objectUnderTest.start();
            topicSender.send(MESSAGE);

            //Assert
            await().atMost(1, TimeUnit.SECONDS).until(() -> !topicListener.getMessages().isEmpty());

            assertTimeout(Duration.ofSeconds(1), objectUnderTest::stop);
        }
    }

    @Test
    void startConfigurableListener()
    {
        //Arrange
        var topicListener = new ConfigurableListener(TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);

        try (  var objectUnderTest = new JMSAdapter(properties) )
        {
            objectUnderTest.register(topicListener);

            ITMessageSender topicSender = new ITMessageSender(properties, TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);
            //Act
            objectUnderTest.start();
            topicSender.send(MESSAGE);

            //Assert
            await().atMost(1, TimeUnit.SECONDS).until(() -> !topicListener.getMessages().isEmpty());

            assertTimeout(Duration.ofSeconds(1), objectUnderTest::stop);
        }
    }



    @Test
    void sharedConnectionListener()
    {
        //Arrange
        var sharedConnectionListener1 = new SharedConnectionListener();
        var sharedConnectionListener2 = new SharedConnectionListener();

        try (  var objectUnderTest = new JMSAdapter(properties) )
        {
            objectUnderTest.register(sharedConnectionListener1);
            objectUnderTest.register(sharedConnectionListener2);

            ITMessageSender topicSender = new ITMessageSender(properties, TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);
            //Act
            objectUnderTest.start();
            topicSender.send(MESSAGE);

            await().atMost(1, TimeUnit.SECONDS).until(() -> !sharedConnectionListener1.getMessages().isEmpty() || !sharedConnectionListener2.getMessages().isEmpty());

            //Assert
            assertTimeout(Duration.ofSeconds(1), objectUnderTest::stop);
            //Since we have shared connection, we should only get one message
            assertEquals(1, sharedConnectionListener1.getMessageCount() + sharedConnectionListener2.getMessageCount());
        }
    }

    @Test
    void unsharedConnectionListener()
    {
        //Arrange
        var topicListener1 = new TopicListener();
        var topicListener2 = new TopicListener();

        try (  var objectUnderTest = new JMSAdapter(properties) )
        {
            objectUnderTest.register(topicListener1);
            objectUnderTest.register(topicListener2);

            ITMessageSender topicSender = new ITMessageSender(properties, TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);
            //Act
            objectUnderTest.start();
            topicSender.send(MESSAGE);

            await().atMost(1, TimeUnit.SECONDS).until(() -> !topicListener1.getMessages().isEmpty() && !topicListener2.getMessages().isEmpty());

            //Assert
            assertTimeout(Duration.ofSeconds(1), objectUnderTest::stop);
            //Since we have shared connection, we should only get one message
            assertEquals(2, topicListener1.getMessageCount() + topicListener2.getMessageCount());
        }
    }

    @Test
    void startJMSAdapterQueue()
    {
        //Arrange
        var queueListener = new QueueListener();

        try (  var objectUnderTest = new JMSAdapter(properties) )
        {
            objectUnderTest.register(queueListener);

            ITMessageSender queueSender = new ITMessageSender(properties, QueueListener.QUEUE_DESTINATION, JMSConfiguration.MessagingType.QUEUE);
            //Act
            objectUnderTest.start();
            queueSender.send(MESSAGE);

            //Assert
            await().atMost(1, TimeUnit.SECONDS).until( () -> !queueListener.getMessages().isEmpty());

            assertTimeout(Duration.ofSeconds(1), objectUnderTest::stop);
        }

    }



    @Test
    void startJMSAdapterJexxa()
    {
        //Arrange
        var messageListener = new TopicListener();

        JexxaMain jexxaMain = new JexxaMain(JexxaTestApplication.class, properties);

        jexxaMain.bind(JMSAdapter.class).to(messageListener)
                .disableBanner()
                .start();

        ITMessageSender myProducer = new ITMessageSender(properties, TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);

        //Act
        myProducer.send(MESSAGE);

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !messageListener.getMessages().isEmpty());

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    void startJMSAdapterJexxaSecretFile() throws IOException
    {
        //Arrange
        var messageListener = new TopicListener();
        var properties = new Properties();
        properties.load(getClass().getResourceAsStream("/jexxa-secrets.properties"));

        JexxaMain jexxaMain = new JexxaMain(JexxaTestApplication.class, properties);

        jexxaMain.bind(JMSAdapter.class).to(messageListener)
                .disableBanner()
                .start();

        ITMessageSender myProducer = new ITMessageSender(jexxaMain.getProperties(), TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);

        //Act
        myProducer.send(MESSAGE);

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !messageListener.getMessages().isEmpty());

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    @Test
    void invalidProperties()
    {
        //1.Assert missing properties
        var emptyProperties = new Properties();
        assertThrows(IllegalArgumentException.class, () ->  new JMSAdapter(emptyProperties));

        //2.Arrange invalid properties: Invalid JNDI_FACTORY_KEY
        Properties propertiesInvalidProvider = new Properties();
        propertiesInvalidProvider.put(JMSProperties.JNDI_PROVIDER_URL_KEY, "invalid");
        propertiesInvalidProvider.put(JMSProperties.JNDI_FACTORY_KEY, DEFAULT_JNDI_FACTORY);

        //2.Assert invalid properties: Invalid Driver
        assertThrows(IllegalArgumentException.class, () -> new JDBCKeyValueRepository<>(
                JexxaEntity.class,
                JexxaEntity::getKey,
                propertiesInvalidProvider
        ));

        //3. Arrange invalid properties: Invalid URL
        Properties propertiesInvalidFactory = new Properties();
        propertiesInvalidFactory.put(JMSProperties.JNDI_PROVIDER_URL_KEY, DEFAULT_JNDI_PROVIDER_URL);
        propertiesInvalidFactory.put(JMSProperties.JNDI_FACTORY_KEY, "invalid");

        //3.Assert invalid properties: Invalid URL
        assertThrows(IllegalArgumentException.class, () -> new JDBCKeyValueRepository<>(
                JexxaEntity.class,
                JexxaEntity::getKey,
                propertiesInvalidFactory
        ));
    }
}
