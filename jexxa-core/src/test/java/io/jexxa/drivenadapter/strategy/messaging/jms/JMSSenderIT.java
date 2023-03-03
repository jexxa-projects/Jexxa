package io.jexxa.drivenadapter.strategy.messaging.jms;


import io.jexxa.TestConstants;
import io.jexxa.application.JexxaTestApplication;
import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.drivenadapter.strategy.messaging.MessageSender;
import io.jexxa.drivenadapter.strategy.messaging.MessageSenderManager;
import io.jexxa.drivenadapter.strategy.outbox.TransactionalOutboxSender;
import io.jexxa.drivingadapter.messaging.JMSAdapter;
import io.jexxa.api.wrapper.utils.messaging.QueueListener;
import io.jexxa.api.wrapper.utils.messaging.TopicListener;
import io.jexxa.utils.properties.JexxaJMSProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static io.jexxa.api.wrapper.utils.messaging.QueueListener.QUEUE_DESTINATION;
import static io.jexxa.api.wrapper.utils.messaging.TopicListener.TOPIC_DESTINATION;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JMSSenderIT
{
    private static final String MESSAGE_SENDER_CONFIG = "getMessageSenderConfig";
    private static final String TYPE = "type";
    private final JexxaValueObject message = new JexxaValueObject(42);

    private TopicListener topicListener;
    private QueueListener queueListener;
    private JexxaMain jexxaMain;


    @BeforeEach
    void initTests()
    {
        jexxaMain = new JexxaMain(JexxaTestApplication.class);
        topicListener = new TopicListener();
        queueListener = new QueueListener();

        jexxaMain.disableBanner()
                .bind(JMSAdapter.class).to(queueListener)
                .bind(JMSAdapter.class).to(topicListener)
                .start();
    }

    @SuppressWarnings("unused")
    static Stream<Class<? extends MessageSender>> getMessageSenderConfig()
    {
        return Stream.of(JMSSender.class, TransactionalOutboxSender.class);
    }

    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void sendMessageToTopic(Class<? extends MessageSender> messageSender)
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(messageSender);
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

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


    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void sendMessageToQueue(Class<? extends MessageSender> messageSender)
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(messageSender);
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

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

    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void sendMessageToQueueAsString(Class<? extends MessageSender> messageSender)
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(messageSender);
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

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


    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void sendByteMessageToTopic(Class<? extends MessageSender> messageSender)
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(messageSender);
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

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

    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void sendByteMessageToQueue(Class<? extends MessageSender> messageSender)
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(messageSender);
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

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
    void sendMessageReconnectQueue() throws JMSException
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(JMSSender.class); // Reconnect is only meaningful for JMSSender
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

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
    void testPasswordFile()
    {
        //Arrange
        var properties = new Properties();
        properties.putAll(jexxaMain.getProperties());
        properties.remove(JexxaJMSProperties.JNDI_PASSWORD_KEY);
        properties.put(JexxaJMSProperties.JNDI_PASSWORD_FILE, "src/test/resources/secrets/jndiPassword");
        MessageSenderManager.setDefaultStrategy(JMSSender.class);

        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, properties);

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