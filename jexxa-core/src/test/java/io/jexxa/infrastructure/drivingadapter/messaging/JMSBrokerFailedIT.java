package io.jexxa.infrastructure.drivingadapter.messaging;

import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.jms.Connection;
import javax.jms.JMSException;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.utils.messaging.MessageSender;
import io.jexxa.infrastructure.utils.messaging.TopicListener;
import org.junit.jupiter.api.Test;

public class JMSBrokerFailedIT
{
    private static final String MESSAGE = "Hello World";

    @Test
    void testReconnect() throws JMSException, InterruptedException
    {
        //Arrange
        var maxSendMessages = 10;
        var currentSendMessages = 0;
        var jexxaMain = new JexxaMain(JMSBrokerFailedIT.class.getSimpleName());
        var messageListener = new TopicListener();
        var jmsAdapter = new JMSAdapter(jexxaMain.getProperties());
        
        var myProducer = new MessageSender(jexxaMain.getProperties(), TopicListener.TOPIC_DESTINATION, JMSConfiguration.MessagingType.TOPIC);

        jmsAdapter.register(messageListener);
        jmsAdapter.start();

        //Act
        simulateConnectionException(jmsAdapter.getConnection());

        //Assert
        while (messageListener.getMessages().isEmpty() && currentSendMessages <= maxSendMessages)
        {
            myProducer.send(MESSAGE); //Regularly send a message
            ++currentSendMessages;
            //noinspection BusyWait
            Thread.sleep(100);
        }

        jmsAdapter.stop();

        assertFalse(messageListener.getMessages().isEmpty());
    }

    private void simulateConnectionException(Connection connection) throws JMSException
    {
        connection.stop();

        connection.getExceptionListener().onException(new JMSException("Simulated error "));
    }
}

