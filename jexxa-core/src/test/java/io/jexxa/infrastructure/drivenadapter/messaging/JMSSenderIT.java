package io.jexxa.infrastructure.drivenadapter.messaging;



import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.jexxa.TestTags;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSListener;
import io.jexxa.utils.JexxaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestTags.INTEGRATION_TEST)
class JMSSenderIT
{
    final JexxaValueObject message = new JexxaValueObject(42);
    final String testName = "JMSSenderIT";
    Properties properties;

    @BeforeEach
    protected void initTests()
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
    protected void sentMessageToTopic()
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

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    @Test
    @Timeout(1)
    protected void sentMessageToQueue()
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

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    static class MyTopicListener implements MessageListener
    {

        final List<Message> messageList = new ArrayList<>();

        @Override
        @JMSListener(destination = "JMSSenderIT", messagingType = JMSListener.MessagingType.TOPIC)
        public void onMessage(Message message)
        {
            try
            {
                JexxaLogger.getLogger(JMSSenderIT.class).info(((TextMessage) message).getText());
                messageList.add(message);
            }
            catch ( JMSException e) {
                JexxaLogger.getLogger(JMSSenderIT.class).error(e.getMessage());
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
                JexxaLogger.getLogger(JMSSenderIT.class).info(((TextMessage) message).getText());
                messageList.add(message);
            }
            catch ( JMSException e) {
                JexxaLogger.getLogger(JMSSenderIT.class).error(e.getMessage());
            }
        }

        List<Message> getMessages()
        {
            return messageList;
        }
    }

}