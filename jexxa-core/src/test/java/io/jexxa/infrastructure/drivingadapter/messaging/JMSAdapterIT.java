package io.jexxa.infrastructure.drivingadapter.messaging;


import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.TestConstants.JEXXA_DRIVEN_ADAPTER;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository;
import io.jexxa.infrastructure.utils.messaging.ITMessageSender;
import io.jexxa.infrastructure.utils.messaging.QueueListener;
import io.jexxa.infrastructure.utils.messaging.TopicListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JMSAdapterIT
{
    private static final String MESSAGE = "Hello World";

    private Properties properties;

    @BeforeEach
    void initTests() throws IOException
    {
        //Arrange
        properties = new Properties();
        properties.load(getClass().getResourceAsStream(JexxaMain.JEXXA_APPLICATION_PROPERTIES));
    }

    @Test
    void startJMSAdapterTopic()
    {
        //Arrange
        var topicListener = new TopicListener();

        try (  var objectUnderTest = new JMSAdapter(properties) )
        {
            objectUnderTest.register(topicListener);

            ITMessageSender topicSender = new ITMessageSender(properties, TopicListener.TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);
            //Act
            objectUnderTest.start();
            topicSender.send(MESSAGE);

            //Assert
            await().atMost(1, TimeUnit.SECONDS).until(() -> !topicListener.getMessages().isEmpty());

            assertTimeout(Duration.ofSeconds(1), objectUnderTest::stop);
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

        JexxaMain jexxaMain = new JexxaMain("JMSAdapterTest", properties);

        jexxaMain.addToApplicationCore(JEXXA_APPLICATION_SERVICE)
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMSAdapter.class).to(messageListener)
                .start();

        ITMessageSender myProducer = new ITMessageSender(properties, TopicListener.TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);

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
        propertiesInvalidProvider.put(JMSAdapter.JNDI_PROVIDER_URL_KEY, "invalid");
        propertiesInvalidProvider.put(JMSAdapter.JNDI_FACTORY_KEY, JMSAdapter.DEFAULT_JNDI_FACTORY);

        //2.Assert invalid properties: Invalid Driver
        assertThrows(IllegalArgumentException.class, () -> new JDBCKeyValueRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                propertiesInvalidProvider
        ));

        //3. Arrange invalid properties: Invalid URL
        Properties propertiesInvalidFactory = new Properties();
        propertiesInvalidFactory.put(JMSAdapter.JNDI_PROVIDER_URL_KEY, JMSAdapter.DEFAULT_JNDI_PROVIDER_URL);
        propertiesInvalidFactory.put(JMSAdapter.JNDI_FACTORY_KEY, "invalid");

        //3.Assert invalid properties: Invalid URL
        assertThrows(IllegalArgumentException.class, () -> new JDBCKeyValueRepository<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                propertiesInvalidFactory
        ));
    }
}
