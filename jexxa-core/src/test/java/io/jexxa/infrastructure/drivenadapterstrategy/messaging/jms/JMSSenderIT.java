package io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms;


import io.jexxa.TestConstants;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.utils.messaging.QueueListener;
import io.jexxa.infrastructure.utils.messaging.TopicListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.TestConstants.JEXXA_DRIVEN_ADAPTER;
import static io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender.JNDI_PASSWORD_FILE;
import static io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms.JMSSender.JNDI_PASSWORD_KEY;
import static io.jexxa.infrastructure.utils.messaging.QueueListener.QUEUE_DESTINATION;
import static io.jexxa.infrastructure.utils.messaging.TopicListener.TOPIC_DESTINATION;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JMSSenderIT
{
    private static final String TYPE = "type";
    private final JexxaValueObject message = new JexxaValueObject(42);

    private TopicListener topicListener;
    private QueueListener queueListener;
    private JexxaMain jexxaMain;

    private MessageSender objectUnderTest;

    @BeforeEach
    void initTests()
    {
        jexxaMain = new JexxaMain(JMSSenderIT.class);
        topicListener = new TopicListener();
        queueListener = new QueueListener();
        objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

        jexxaMain.addToApplicationCore(JEXXA_APPLICATION_SERVICE)
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMSAdapter.class).to(queueListener)
                .bind(JMSAdapter.class).to(topicListener)
                .start();
    }


    @Test
    void sendMessageToTopic()
    {
        //Arrange --

        //Act
        objectUnderTest
                .send(message)
                .toTopic(TOPIC_DESTINATION)
                .addHeader(TYPE, message.getClass().getSimpleName())
                .asJson();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !topicListener.getMessages().isEmpty());

        assertDoesNotThrow(() -> (TextMessage)topicListener.getMessages().get(0));

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    @Test
    void sendMessageToQueue()
    {
        //Arrange --

        //Act
        objectUnderTest
                .send(message)
                .toQueue(QUEUE_DESTINATION)
                .addHeader(TYPE, message.getClass().getSimpleName())
                .asJson();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !queueListener.getMessages().isEmpty());
        assertDoesNotThrow(() -> (TextMessage)queueListener.getMessages().get(0));

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    void sendMessageToQueueAsString()
    {
        //Arrange --

        //Act
        objectUnderTest
                .send(message)
                .toQueue(QUEUE_DESTINATION)
                .addHeader(TYPE, message.getClass().getSimpleName())
                .asString();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !queueListener.getMessages().isEmpty());
        assertDoesNotThrow(() -> (TextMessage)queueListener.getMessages().get(0));

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    void sendMessageReconnectQueue() throws JMSException
    {
        //Arrange --

        //Act (simulate an error in between sending two messages
        objectUnderTest
                .send(message)
                .toQueue(QUEUE_DESTINATION)
                .asJson();

        //Simulate the error
        simulateConnectionException(((JMSSender) (objectUnderTest)).getConnection());

        objectUnderTest
                .send(message)
                .toQueue(QUEUE_DESTINATION)
                .asJson();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> queueListener.getMessages().size() >= 2);
        assertDoesNotThrow(() -> (TextMessage)queueListener.getMessages().get(0));

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    void sendByteMessageToTopic()
    {
        //Arrange --

        //Act
        objectUnderTest
                .sendByteMessage(message)
                .toTopic(TOPIC_DESTINATION)
                .addHeader(TYPE, message.getClass().getSimpleName())
                .asJson();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !topicListener.getMessages().isEmpty());

        assertDoesNotThrow(() -> (BytesMessage)topicListener.getMessages().get(0));
        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    void sendByteMessageToQueue()
    {
        //Arrange --

        //Act
        objectUnderTest
                .sendByteMessage(message)
                .toQueue(QUEUE_DESTINATION)
                .addHeader(TYPE, message.getClass().getSimpleName())
                .asJson();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !queueListener.getMessages().isEmpty());

        assertDoesNotThrow(() -> (BytesMessage)queueListener.getMessages().get(0));
        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    void testPasswordFile()
    {
        //Arrange
        var properties = new Properties();
        properties.putAll(jexxaMain.getProperties());
        properties.remove(JNDI_PASSWORD_KEY);
        properties.put(JNDI_PASSWORD_FILE, "src/test/resources/secrets/jndiPassword");

        objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, properties);

        //Act
        objectUnderTest
                .sendByteMessage(message)
                .toQueue(QUEUE_DESTINATION)
                .addHeader(TYPE, message.getClass().getSimpleName())
                .asJson();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !queueListener.getMessages().isEmpty());

        assertDoesNotThrow(() -> (BytesMessage)queueListener.getMessages().get(0));
        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    private void simulateConnectionException(Connection connection) throws JMSException
    {
        var listener = connection.getExceptionListener();

        connection.close();

        listener.onException(new JMSException("Simulated error "));
    }
}