package io.jexxa.infrastructure.drivenadapterstrategy.messaging;



import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.TestConstants.JEXXA_DRIVEN_ADAPTER;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import io.jexxa.TestConstants;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSAdapter;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.utils.JexxaLogger;
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

    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    @Test
    @Timeout(1)
    void sentMessageToTopic()
    {
        //Arrange
        var messageListener = new MyTopicListener();
        var objectUnderTest = new JMSSender(jexxaMain.getProperties());

        jexxaMain.addToApplicationCore(JEXXA_APPLICATION_SERVICE)
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMSAdapter.class).to(messageListener)
                .start();

        //Act
        objectUnderTest.sendToTopic(message, JMSSenderIT.class.getSimpleName(), null);

        //Assert
        while (messageListener.getMessages().isEmpty())
        {
            Thread.onSpinWait();
        }

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    @Test
    @Timeout(2)
    void sentMessageToQueue()
    {
        //Arrange
        var messageListener = new MyQueueListener();
        var objectUnderTest = new JMSSender(jexxaMain.getProperties());

        jexxaMain.addToApplicationCore(JEXXA_APPLICATION_SERVICE)
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .bind(JMSAdapter.class).to(messageListener)
                .start();

        //Act
        objectUnderTest.sendToQueue(message, JMSSenderIT.class.getSimpleName(), null);

        //Assert
        while (messageListener.getMessages().isEmpty())
        {
            Thread.onSpinWait();
        }

        assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    static class MyTopicListener implements MessageListener
    {

        private final List<Message> messageList = new ArrayList<>();

        @Override
        @JMSConfiguration(destination = "JMSSenderIT", messagingType = JMSConfiguration.MessagingType.TOPIC)
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

        private final List<Message> messageList = new ArrayList<>();

        @Override
        @JMSConfiguration(destination = "JMSSenderIT", messagingType = JMSConfiguration.MessagingType.QUEUE)
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