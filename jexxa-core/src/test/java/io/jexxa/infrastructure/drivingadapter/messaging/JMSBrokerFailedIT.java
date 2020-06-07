package io.jexxa.infrastructure.drivingadapter.messaging;

import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.jms.Connection;
import javax.jms.JMSException;

import io.jexxa.core.JexxaMain;
import org.junit.jupiter.api.Test;

public class JMSBrokerFailedIT
{
    @Test
    protected void testReconnect() throws JMSException
    {
        //Arrange
        var maxSendMessages = 10;
        var currentSendMessages = 0;
        var jexxaMain = new JexxaMain(JMSBrokerFailedIT.class.getSimpleName());
        var messageListener = new JMSAdapterIT.MyListener();
        var jmsAdapter = new JMSAdapter(jexxaMain.getProperties());
        
        var myProducer = new JMSAdapterIT.MyProducer(jexxaMain.getProperties());

        jmsAdapter.register(messageListener);
        jmsAdapter.start();

        //Act
        simulateConnectionException(jmsAdapter.getConnection());

        //Assert
        while (messageListener.getMessages().isEmpty() && currentSendMessages <= maxSendMessages)
        {
            myProducer.sendToTopic(); //Regularly send a message
            ++currentSendMessages;
            Thread.onSpinWait();
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

