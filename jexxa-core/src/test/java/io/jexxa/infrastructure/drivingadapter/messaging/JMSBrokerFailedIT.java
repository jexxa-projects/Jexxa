package io.jexxa.infrastructure.drivingadapter.messaging;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.JMSException;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.utils.messaging.ITMessageSender;
import io.jexxa.infrastructure.utils.messaging.TopicListener;
import org.junit.jupiter.api.Test;

class JMSBrokerFailedIT
{
    private static final String MESSAGE = "Hello World";

    @Test
    void testReconnect() throws JMSException
    {
        //Arrange
        var jexxaMain = new JexxaMain(JMSBrokerFailedIT.class.getSimpleName());
        var messageListener = new TopicListener();
        var jmsAdapter = new JMSAdapter(jexxaMain.getProperties());
        
        var myProducer = new ITMessageSender(jexxaMain.getProperties(), TopicListener.TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);

        jmsAdapter.register(messageListener);
        jmsAdapter.start();

        //Act
        simulateConnectionException(jmsAdapter.getConnection());

        var service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> myProducer.send(MESSAGE),100, 100, TimeUnit.MILLISECONDS ); // Send messages in a 100 ms interval

        //Assert
        await().atMost(Duration.ofSeconds(2,0)).until(() -> !messageListener.getMessages().isEmpty());

        service.shutdown();
        jmsAdapter.stop();

        assertFalse(messageListener.getMessages().isEmpty());
    }

    private void simulateConnectionException(Connection connection) throws JMSException
    {
        connection.stop();

        connection.getExceptionListener().onException(new JMSException("Simulated error "));
    }
}

