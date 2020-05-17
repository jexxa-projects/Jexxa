package io.jexxa.infrastructure.drivingadapter.messaging;

import static io.jexxa.TestTags.INTEGRATION_TEST;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import io.jexxa.core.JexxaMain;
import io.jexxa.utils.JexxaLogger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(INTEGRATION_TEST)
public class MultipleJMSReceiverIT
{
    static final int MAX_COUNTER = 1000;
    static final int MAX_THREADS = 5;

    private ApplicationService applicationService;

    static public class ApplicationService
    {
        private int counter = 0;
        private final List<Integer> usedCounter = new ArrayList<>();

        @SuppressWarnings("unused")
        public void increment()
        {
            if ( counter < MAX_COUNTER )
            {
                ++counter;
                usedCounter.add(counter);
            }
        }

        public int getCounter()
        {
            return counter;
        }

        public List<Integer> getUsedCounter()
        {
            return usedCounter;
        }
    }

    public static class ApplicationServiceListener implements MessageListener
    {
        private final ApplicationService applicationService;

        public ApplicationServiceListener(ApplicationService applicationService)
        {
            this.applicationService = applicationService;
        }

        @Override
        @JMSListener(destination = "ApplicationServiceListener", messagingType = JMSListener.MessagingType.TOPIC)
        public void onMessage(Message message)
        {
            try
            {
                //JexxaLogger.getLogger(JMSAdapterIT.MyListener.class).info(((TextMessage) message).getText());
                applicationService.increment();
            }
            catch (Exception e) {
                JexxaLogger.getLogger(ApplicationServiceListener.class).error(e.getMessage());
            }
        }
    }



    @Test
    public void synchronizeMultipleClients() throws Exception
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain("MultiThreading");

        for ( int i = 0; i < MAX_THREADS; ++i)
        {
            jexxaMain.bind(JMSAdapter.class).to(ApplicationServiceListener.class);
        }
        applicationService = jexxaMain.getInstanceOfPort(ApplicationService.class);
        List<Integer> expectedResult = IntStream.rangeClosed(1, MAX_COUNTER)
                .boxed()
                .collect(toList());

        jexxaMain.start();

        //Act
        incrementService(jexxaMain.getProperties());
        
        //Assert
        jexxaMain.stop();

        Assertions.assertEquals(expectedResult, applicationService.getUsedCounter());
    }

    private void incrementService(Properties properties) throws Exception
    {
        MyProducer myProducer = new MyProducer(properties);
        while ( applicationService.getCounter() < MAX_COUNTER )
        {
            //Act
            myProducer.sendToTopic();
        }
    }



    public static class MyProducer implements AutoCloseable
    {
        final Connection connection;
        final Session session;
        final MessageProducer producer;

        MyProducer(Properties properties) throws Exception
        {
            JMSAdapter jmsAdapter = new JMSAdapter(properties);
            try
            {
                this.connection = jmsAdapter.createConnection();
                connection.start();

                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic("ApplicationServiceListener");

                producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            } catch (JMSException e)
            {
               throw new Exception(e);
            }
        }

        public void sendToTopic()
        {
            try
            {

                String text = "Hello world";
                TextMessage message = session.createTextMessage(text);

                producer.send(message);

            }
            catch (JMSException e)
            {
                JexxaLogger.getLogger(JMSAdapterIT.MyProducer.class).error(e.getMessage());
            }
        }

        public void close()
        {
            try
            {
                if (session != null)
                {
                    session.close();
                }
            } catch (Exception e)
            {
                System.out.println(e.getMessage());
            }

            try
            {
                if (connection != null) {
                    connection.close();
                }
             } catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }

    }

}
