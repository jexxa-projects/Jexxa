package io.jexxa.infrastructure.drivenadapter.messaging;


import static io.jexxa.TestTags.INTEGRATION_TEST;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapterIT;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSListener;
import io.jexxa.utils.JexxaLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(INTEGRATION_TEST)
public class JMSSenderIT
{
    final JexxaValueObject message = new JexxaValueObject(42);
    final String testName = "JMSSenderIT";
    Properties properties;

    @BeforeEach
    void initTests()
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
    void sentMessageToTopic()
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

        Assertions.assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    @Test
    @Timeout(1)
    void sentMessageToQueue()
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

        Assertions.assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    static public class MyTopicListener implements MessageListener
    {

        final List<Message> messageList = new ArrayList<>();

        @Override
        @JMSListener(destination = "JMSSenderIT", messagingType = JMSListener.MessagingType.TOPIC)
        public void onMessage(Message message)
        {
            try
            {
                JexxaLogger.getLogger(JMSAdapterIT.MyListener.class).info(((TextMessage) message).getText());
                messageList.add(message);
            }
            catch ( JMSException e) {
                JexxaLogger.getLogger(JMSAdapterIT.MyListener.class).error(e.getMessage());
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
        @JMSListener(destination = "JMSSenderIT", messagingType = JMSListener.MessagingType.QUEUE)
        public void onMessage(Message message)
        {
            try
            {
                JexxaLogger.getLogger(JMSAdapterIT.MyListener.class).info(((TextMessage) message).getText());
                messageList.add(message);
            }
            catch ( JMSException e) {
                JexxaLogger.getLogger(JMSAdapterIT.MyListener.class).error(e.getMessage());
            }
        }

        public List<Message> getMessages()
        {
            return messageList;
        }
    }

}