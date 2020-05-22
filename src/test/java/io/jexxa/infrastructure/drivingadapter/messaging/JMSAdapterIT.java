package io.jexxa.infrastructure.drivingadapter.messaging;

import static io.jexxa.TestTags.INTEGRATION_TEST;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapter.persistence.jdbc.JDBCConnection;
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
public class JMSAdapterIT
{
    private Properties properties;

    @BeforeEach
    void initTests() throws IOException
    {
        //Arrange
        properties = new Properties();
        properties.load(getClass().getResourceAsStream(JexxaMain.JEXXA_APPLICATION_PROPERTIES));

    }

    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    @Test
    @Timeout(1)
    void startJMSAdapter()
    {
        //Arrange
        var messageListener = new MyListener();

        try (  var objectUnderTest = new JMSAdapter(properties) )
        {
            objectUnderTest.register(messageListener);

            MyProducer myProducer = new MyProducer(properties);
            //Act
            objectUnderTest.start();
            myProducer.sendToTopic();

            //Assert
            while (messageListener.getMessages().isEmpty())
            {
                Thread.onSpinWait();
            }

            Assertions.assertTimeout(Duration.ofSeconds(1), objectUnderTest::stop);
        }

    }



    @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
    @Test
    @Timeout(1)
    void startJMSAdapterJexxa()
    {
        //Arrange
        var messageListener = new MyListener();

        JexxaMain jexxaMain = new JexxaMain("JMSAdapterTest", properties);
        jexxaMain.bind(JMSAdapter.class).to(messageListener);
        
        MyProducer myProducer = new MyProducer(properties);

        //Act
        jexxaMain.start();
        myProducer.sendToTopic();

        //Assert
        while (messageListener.getMessages().isEmpty())
        {
            Thread.onSpinWait();
        }

        Assertions.assertTimeout(Duration.ofSeconds(1), jexxaMain::stop);
    }


    @Test
    void invalidProperties()
    {
        //1.Assert missing properties
        Assertions.assertThrows(IllegalArgumentException.class, () ->  new JMSAdapter(new Properties()));

        //2.Arrange invalid properties: Invalid JNDI_FACTORY_KEY
        Properties propertiesInvalidProvider = new Properties();
        propertiesInvalidProvider.put(JMSAdapter.JNDI_PROVIDER_URL_KEY, "invalid");
        propertiesInvalidProvider.put(JMSAdapter.JNDI_FACTORY_KEY, JMSAdapter.DEFAULT_JNDI_FACTORY);

        //2.Assert invalid properties: Invalid Driver
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JDBCConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                propertiesInvalidProvider
        ));

        //3. Arrange invalid properties: Invalid URL
        Properties propertiesInvalidFactory = new Properties();
        propertiesInvalidFactory.put(JMSAdapter.JNDI_PROVIDER_URL_KEY, JMSAdapter.DEFAULT_JNDI_PROVIDER_URL);
        propertiesInvalidFactory.put(JMSAdapter.JNDI_FACTORY_KEY, "invalid");

        //3.Assert invalid properties: Invalid URL
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JDBCConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                propertiesInvalidFactory
        ));
    }



    public static class MyListener implements MessageListener
    {

        final List<Message> messageList = new ArrayList<>();

        @Override
        @JMSListener(destination = "MyListener", messagingType = JMSListener.MessagingType.TOPIC)
        public void onMessage(Message message)
        {
            try
            {
                JexxaLogger.getLogger(MyListener.class).info(((TextMessage) message).getText());
                messageList.add(message);
            }
            catch (JMSException e) {
                JexxaLogger.getLogger(MyListener.class).error(e.getMessage());
            }
        }

        public List<Message> getMessages()
        {
            return messageList;
        }
    }

    public static class MyProducer {
        final Connection connection;
        MyProducer(Properties properties)
        {
            JMSAdapter jmsAdapter = new JMSAdapter(properties);
            this.connection = jmsAdapter.createConnection();
        }
        public void sendToTopic() {
            try {
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic(MyListener.class.getSimpleName());

                MessageProducer producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                String text = "Hello world" ;
                TextMessage message = session.createTextMessage(text);

                producer.send(message);

                session.close();
                connection.close();
            }
            catch (JMSException e) {
                JexxaLogger.getLogger(MyProducer.class).error(e.getMessage());
            }
        }
    }

}
