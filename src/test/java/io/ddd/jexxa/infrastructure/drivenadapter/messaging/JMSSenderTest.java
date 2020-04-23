package io.ddd.jexxa.infrastructure.drivenadapter.messaging;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import io.ddd.jexxa.core.JexxaMain;
import io.ddd.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.ddd.jexxa.infrastructure.drivingadapter.messaging.JMSAdapterTest;
import io.ddd.jexxa.infrastructure.drivingadapter.messaging.JMSListener;
import io.ddd.jexxa.utils.JexxaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
public class JMSSenderTest
{
    final JexxaValueObject message = new JexxaValueObject(42);
    final String testName = "JMSSenderTest";
    Properties properties;

    @BeforeEach
    public void initTests()
    {
        properties = new Properties();
        properties.put(JMSSender.JNDI_FACTORY_KEY, JMSSender.DEFAULT_JNDI_FACTORY);
        properties.put(JMSSender.JNDI_PROVIDER_URL_KEY, JMSSender.DEFAULT_JNDI_PROVIDER_URL);
        properties.put(JMSSender.JNDI_USER_KEY, JMSSender.DEFAULT_JNDI_USER);
        properties.put(JMSSender.JNDI_PASSWORD_KEY, JMSSender.DEFAULT_JNDI_PASSWORD);
    }

    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    @Test
    @Timeout(1)
    public void sentMessageToTopic()
    {
        //Arrange
        var messageListener = new MyTopicListener();
        var objectUnderTest = new JMSSender(properties);
        var jexxaMain = new JexxaMain(testName, properties);
        jexxaMain.bind(JMSAdapter.class).to(messageListener);
        jexxaMain.start();

        //Act
        objectUnderTest.sendToTopic(message, testName, null);

        //Assert
        while (messageListener.getMessages().isEmpty())
        {
            Thread.onSpinWait();
        }

        jexxaMain.stop();
    }


    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    @Test
    @Timeout(1)
    public void sentMessageToQueue()
    {
        //Arrange
        var messageListener = new MyQueueListener();
        var objectUnderTest = new JMSSender(properties);
        var jexxaMain = new JexxaMain(testName, properties);
        jexxaMain.bind(JMSAdapter.class).to(messageListener);
        jexxaMain.start();

        //Act
        objectUnderTest.sendToQueue(message, testName, null);

        //Assert
        while (messageListener.getMessages().isEmpty())
        {
            Thread.onSpinWait();
        }

        jexxaMain.stop();
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
            catch ( JMSException e) {
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
            catch ( JMSException e) {
                JexxaLogger.getLogger(JMSAdapterTest.MyListener.class).error(e.getMessage());
            }
        }

        public List<Message> getMessages()
        {
            return messageList;
        }
    }

}