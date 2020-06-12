package io.jexxa.infrastructure.drivenadapterstrategy.messaging;


import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.TestConstants.JEXXA_DRIVEN_ADAPTER;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.JMSException;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.utils.messaging.QueueListener;
import io.jexxa.infrastructure.utils.messaging.TopicListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JMSSenderIT
{
    private final JexxaValueObject message = new JexxaValueObject(42);
    private JexxaMain jexxaMain;

    @BeforeEach
    void initTests()
    {
        jexxaMain = new JexxaMain(JMSSenderIT.class.getSimpleName());
    }

    @Test
    @Timeout(1)
    void sendMessageToTopic()
    {
        //Arrange
        var messageListener = new TopicListener();
        var objectUnderTest = new JMSSender(jexxaMain.getProperties());

        jexxaMain.addToApplicationCore(JEXXA_APPLICATION_SERVICE)
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMSAdapter.class).to(messageListener)
                .start();

        //Act
        objectUnderTest.sendToTopic(message, TopicListener.TOPIC_DESTINATION);

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !messageListener.getMessages().isEmpty());

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    @Test
    @Timeout(2)
    void sendMessageToQueue()
    {
        //Arrange
        var messageListener = new QueueListener();
        var objectUnderTest = new JMSSender(jexxaMain.getProperties());

        jexxaMain.addToApplicationCore(JEXXA_APPLICATION_SERVICE)
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMSAdapter.class).to(messageListener)
                .start();

        //Act
        objectUnderTest.sendToQueue(message, QueueListener.QUEUE_DESTINATION);

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> !messageListener.getMessages().isEmpty());

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    @Test
    @Timeout(1)
    void sendMessageReconnectQueue() throws JMSException
    {
        //Arrange
        var messageListener = new QueueListener();
        var objectUnderTest = new JMSSender(jexxaMain.getProperties());

        jexxaMain.addToApplicationCore(JEXXA_APPLICATION_SERVICE)
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMSAdapter.class).to(messageListener)
                .start();

        //Act (simulate an error in between sending two messages
        objectUnderTest.sendToQueue(message, QueueListener.QUEUE_DESTINATION);
        simulateConnectionException(objectUnderTest.getConnection());
        objectUnderTest.sendToQueue(message, QueueListener.QUEUE_DESTINATION);

        //Assert
        await().atMost(1, TimeUnit.SECONDS).until(() -> messageListener.getMessages().size() >= 2);

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }

    private void simulateConnectionException(Connection connection) throws JMSException
    {
        var listener = connection.getExceptionListener();

        connection.close();

        listener.onException(new JMSException("Simulated error "));
    }
}