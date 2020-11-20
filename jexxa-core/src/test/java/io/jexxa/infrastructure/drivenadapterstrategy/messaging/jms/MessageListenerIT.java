package io.jexxa.infrastructure.drivenadapterstrategy.messaging.jms;

import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.TestConstants.JEXXA_DRIVEN_ADAPTER;
import static io.jexxa.infrastructure.utils.messaging.QueueListener.QUEUE_DESTINATION;
import static io.jexxa.infrastructure.utils.messaging.TopicListener.TOPIC_DESTINATION;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.domainevent.JexxaDomainEvent;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.infrastructure.drivingadapter.messaging.listener.DomainEventListener;
import io.jexxa.infrastructure.drivingadapter.messaging.listener.PublishedDomainEvent;
import io.jexxa.infrastructure.drivingadapter.messaging.listener.TypedMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class MessageListenerIT
{
    private final JexxaValueObject message = new JexxaValueObject(42);
    private final JexxaDomainEvent domainEvent = JexxaDomainEvent.create(message);


    private TopicDomainEventListener domainEventListener;
    private TypedListener typedListener;
    private JexxaMain jexxaMain;

    private MessageSender objectUnderTest;

    @BeforeEach
    void initTests()
    {
        jexxaMain = new JexxaMain(MessageListenerIT.class.getSimpleName());
        domainEventListener = new TopicDomainEventListener();
        typedListener = new TypedListener();
        objectUnderTest = MessageSenderManager.getInstance().getStrategy(jexxaMain.getProperties());

        jexxaMain.addToApplicationCore(JEXXA_APPLICATION_SERVICE)
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMSAdapter.class).to(typedListener)
                .bind(JMSAdapter.class).to(domainEventListener)
                .start();
    }

    @Test
    void receiveDomainEvent()
    {
        //Arrange --
        //Act
        objectUnderTest
                .send(domainEvent)
                .toTopic(TOPIC_DESTINATION)
                .asDomainEvent();

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> domainEventListener.getJexxaDomainEvent() != null);
        assertNotNull(domainEventListener.getPublishedDomainEvent());
        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    void receiveTypedMessage()
    {
        //Arrange --
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


    private static class TopicDomainEventListener extends DomainEventListener<JexxaDomainEvent>
    {
        private JexxaDomainEvent jexxaDomainEvent;
        private PublishedDomainEvent publishedDomainEvent;

        public TopicDomainEventListener()
        {
            super(JexxaDomainEvent.class);
        }

        @Override
        @JMSConfiguration(destination = TOPIC_DESTINATION, messagingType = JMSConfiguration.MessagingType.TOPIC)
        public void onDomainEvent(JexxaDomainEvent domainEvent)
        {
            this.jexxaDomainEvent = domainEvent;
            this.publishedDomainEvent = getDomainEvent();
        }

        public JexxaDomainEvent getJexxaDomainEvent()
        {
            return jexxaDomainEvent;
        }

        public PublishedDomainEvent getPublishedDomainEvent()
        {
            return publishedDomainEvent;
        }
    }

    private static class TypedListener extends TypedMessageListener<JexxaValueObject>
    {
        private JexxaValueObject jexxaValueObject;

        public TypedListener()
        {
            super(JexxaValueObject.class);
        }

        @Override
        @JMSConfiguration(destination = QUEUE_DESTINATION, messagingType = JMSConfiguration.MessagingType.QUEUE)
        public void onMessage(JexxaValueObject jexxaValueObject)
        {
            this.jexxaValueObject = jexxaValueObject;
        }

        public JexxaValueObject getJexxaValueObject()
        {
            return jexxaValueObject;
        }
    }



}
