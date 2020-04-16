package io.ddd.jexxa.infrastructure.drivenadapter.messaging;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import io.ddd.jexxa.core.JexxaMain;
import io.ddd.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.messaging.JMSAdapterTest;
import io.ddd.jexxa.infrastructure.drivingadapter.messaging.JMSListener;
import io.ddd.jexxa.utils.JexxaLogger;
import org.junit.Before;
import org.junit.Test;

public class JMSSenderTest
{
    final JexxaValueObject message = new JexxaValueObject(42);
    final String destination = "JMSSenderTest";
    Properties properties;

    @Before
    public void initTests()
    {
        properties = new Properties();
        properties.put(JMSSender.JNDI_FACTORY_KEY, JMSSender.DEFAULT_JNDI_FACTORY);
        properties.put(JMSSender.JNDI_PROVIDER_URL_KEY, JMSSender.DEFAULT_JNDI_PROVIDER_URL);
        properties.put(JMSSender.JNDI_USER_KEY, JMSSender.DEFAULT_JNDI_USER);
        properties.put(JMSSender.JNDI_PASSWORD_KEY, JMSSender.DEFAULT_JNDI_PASSWORD);
    }

    @Test (timeout = 1000)
    public void sentMessageToTopic()
    {
        //Arrange
        var messageListener = new MyTopicListener();
        var objectUnderTest = new JMSSender(properties);
        var jexxaMain = new JexxaMain("JMSSenderTest", properties);
        jexxaMain.bindToPort(JMSAdapter.class, messageListener);
        jexxaMain.startDrivingAdapters();

        //Act
        objectUnderTest.sendToTopic(message, destination, null);

        //Assert
        while (messageListener.getMessages().isEmpty())
        {
            Thread.onSpinWait();
        }

        jexxaMain.stopDrivingAdapters();
    }


    @Test (timeout = 1000)
    public void sentMessageToQueue()
    {
        //Arrange
        var messageListener = new MyQueueListener();
        var objectUnderTest = new JMSSender(properties);
        var jexxaMain = new JexxaMain("JMSSenderTest", properties);
        jexxaMain.bindToPort(JMSAdapter.class, messageListener);
        jexxaMain.startDrivingAdapters();

        //Act
        objectUnderTest.sendToQueue(message, destination, null);

        //Assert
        while (messageListener.getMessages().isEmpty())
        {
            Thread.onSpinWait();
        }

        jexxaMain.stopDrivingAdapters();
    }


    static public class MyTopicListener implements MessageListener
    {

        final List<Message> messageList = new ArrayList<>();

        @Override
        @JMSListener(destination = "JMSSenderTest", messagingType = JMSListener.MessagingType.TOPIC)
        public void onMessage(Message message)
        {
            try
            {
                JexxaLogger.getLogger(JMSAdapterTest.MyListener.class).info(((TextMessage) message).getText());
                messageList.add(message);
            }
            catch ( Exception e) {
                JexxaLogger.getLogger(JMSAdapterTest.MyListener.class).error(e.getMessage());
            }
        }

        public List<Message> getMessages()
        {
            return messageList;
        }
    }

    static public class MyQueueListener implements MessageListener
    {

        final List<Message> messageList = new ArrayList<>();

        @Override
        @JMSListener(destination = "JMSSenderTest", messagingType = JMSListener.MessagingType.QUEUE)
        public void onMessage(Message message)
        {
            try
            {
                JexxaLogger.getLogger(JMSAdapterTest.MyListener.class).info(((TextMessage) message).getText());
                messageList.add(message);
            }
            catch ( Exception e) {
                JexxaLogger.getLogger(JMSAdapterTest.MyListener.class).error(e.getMessage());
            }
        }

        public List<Message> getMessages()
        {
            return messageList;
        }
    }

}