package io.jexxa.infrastructure.messaging.jms;

import io.jexxa.TestConstants;
import io.jexxa.testapplication.JexxaTestApplication;
import io.jexxa.testapplication.domain.model.JexxaDomainEvent;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.messaging.MessageSender;
import io.jexxa.infrastructure.MessageSenderManager;
import io.jexxa.infrastructure.outbox.TransactionalOutboxSender;
import io.jexxa.drivingadapter.messaging.JMSAdapter;
import io.jexxa.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.drivingadapter.messaging.listener.IdempotentListener;
import io.jexxa.drivingadapter.messaging.listener.JSONMessageListener;
import io.jexxa.drivingadapter.messaging.listener.TypedMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static io.jexxa.drivingadapter.messaging.listener.QueueListener.QUEUE_DESTINATION;
import static io.jexxa.drivingadapter.messaging.listener.TopicListener.TOPIC_DESTINATION;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class MessageListenerIT
{
    private static final String MESSAGE_SENDER_CONFIG = "getMessageSenderConfig";
    private final JexxaValueObject message = new JexxaValueObject(42);
    private final JexxaDomainEvent domainEvent = JexxaDomainEvent.create(message);


    private JexxaValueObjectListener typedListener;
    private TextMessageListener jsonMessageListener;
    private JexxaValueObjectIdempotentListener idempotentListener;
    private JexxaMain jexxaMain;


    @BeforeEach
    void initTests()
    {
        jexxaMain = new JexxaMain(JexxaTestApplication.class);
        jsonMessageListener = new TextMessageListener();
        typedListener = new JexxaValueObjectListener();
        idempotentListener = new JexxaValueObjectIdempotentListener();

        jexxaMain.bind(JMSAdapter.class).to(typedListener)
                .bind(JMSAdapter.class).to(idempotentListener)
                .bind(JMSAdapter.class).to(jsonMessageListener)
                .disableBanner()
                .start();
    }
    @SuppressWarnings("unused")
    static Stream<Class<? extends MessageSender>> getMessageSenderConfig()
    {
        return Stream.of(JMSSender.class, TransactionalOutboxSender.class);
    }

    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void receiveDomainEvent(Class<? extends MessageSender> messageSender)
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(messageSender);
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

        //Act
        objectUnderTest
                .send(domainEvent)
                .toTopic(TOPIC_DESTINATION)
                .asJson();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> jsonMessageListener.getTextMessage() != null);

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @ParameterizedTest
    @MethodSource(MESSAGE_SENDER_CONFIG)
    void receiveTypedMessage(Class<? extends MessageSender> messageSender)
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(messageSender);
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());

        //Act
        objectUnderTest
                .send(message)
                .toQueue(QUEUE_DESTINATION)
                .asJson();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> typedListener.getJexxaValueObject() != null);
        assertEquals(message, typedListener.getJexxaValueObject());
        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    void validateIdempotentMessaging()
    {
        //Arrange
        MessageSenderManager.setDefaultStrategy(JMSSender.class);
        var objectUnderTest = MessageSenderManager.getMessageSender(JMSSenderIT.class, jexxaMain.getProperties());
        UUID uuid = UUID.randomUUID();

        //Act
        objectUnderTest
                .send(message)
                .toTopic(TOPIC_DESTINATION)
                .addHeader("domain_event_id", uuid.toString())
                .asJson();

        objectUnderTest
                .send(message)
                .toTopic(TOPIC_DESTINATION)
                .addHeader("domain_event_id", uuid.toString())
                .asJson();

        //Assert - 2 Messages must be received from jsonMessageListener but only one from idempotentListener due to same ID
        await().atMost(2, TimeUnit.SECONDS).until(() -> jsonMessageListener.getReceivedMessages().size() == 2);
        assertEquals(1, idempotentListener.getReceivedMessages().size());
        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }



    private static class TextMessageListener extends JSONMessageListener
    {
        private final List<String> receivedMessages = new ArrayList<>();

        private String textMessage;

        @SuppressWarnings("unused")
        @Override
        @JMSConfiguration(destination = TOPIC_DESTINATION, messagingType = JMSConfiguration.MessagingType.TOPIC)
        public void onMessage(String textMessage)
        {
            this.textMessage = textMessage;
            receivedMessages.add(textMessage);
        }

        public String getTextMessage()
        {
            return textMessage;
        }
        public List<String> getReceivedMessages() {
            return receivedMessages;
        }

    }

    private static class JexxaValueObjectListener extends TypedMessageListener<JexxaValueObject>
    {
        private JexxaValueObject jexxaValueObject;

        public JexxaValueObjectListener()
        {
            super(JexxaValueObject.class);
        }

        @SuppressWarnings("unused")
        @Override
        @JMSConfiguration(destination = QUEUE_DESTINATION, messagingType = JMSConfiguration.MessagingType.QUEUE)
        public void onMessage(JexxaValueObject jexxaValueObject)
        {
            assertTrue(messageContains("valueInPercent"));
            this.jexxaValueObject = jexxaValueObject;
        }

        public JexxaValueObject getJexxaValueObject()
        {
            return jexxaValueObject;
        }

    }

    private static class JexxaValueObjectIdempotentListener extends IdempotentListener<JexxaDomainEvent>
    {
        private final List<JexxaDomainEvent> receivedMessages = new ArrayList<>();

        protected JexxaValueObjectIdempotentListener() {
            super(JexxaDomainEvent.class);
        }

        @Override
        @JMSConfiguration(destination = TOPIC_DESTINATION, messagingType = JMSConfiguration.MessagingType.TOPIC)
        public void onMessage(JexxaDomainEvent message) {
            receivedMessages.add(message);
        }

        public List<JexxaDomainEvent> getReceivedMessages() {
            return receivedMessages;
        }
    }

}
