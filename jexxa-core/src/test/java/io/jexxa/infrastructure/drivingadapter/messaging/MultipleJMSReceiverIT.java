package io.jexxa.infrastructure.drivingadapter.messaging;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

import io.jexxa.TestTags;
import io.jexxa.application.applicationservice.IncrementApplicationService;
import io.jexxa.core.JexxaMain;
import io.jexxa.utils.JexxaLogger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestTags.INTEGRATION_TEST)
class MultipleJMSReceiverIT
{
    private static final int MAX_COUNTER = 1000;
    private static final int MAX_THREADS = 5;

    private IncrementApplicationService incrementApplicationService;


    public static class ApplicationServiceListener implements MessageListener
    {
        private final IncrementApplicationService incrementApplicationService;

        public ApplicationServiceListener(IncrementApplicationService incrementApplicationService)
        {
            this.incrementApplicationService = incrementApplicationService;
        }

        @Override
        @JMSListener(destination = "ApplicationServiceListener", messagingType = JMSListener.MessagingType.TOPIC)
        public void onMessage(Message message)
        {
            try
            {
                incrementApplicationService.increment();
            }
            catch (Exception e) {
                JexxaLogger.getLogger(ApplicationServiceListener.class).error(e.getMessage());
            }
        }
    }



    @Test
    protected void synchronizeMultipleClients() 
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain("MultiThreading");

        for ( int i = 0; i < MAX_THREADS; ++i)
        {
            jexxaMain.bind(JMSAdapter.class).to(ApplicationServiceListener.class);
        }
        incrementApplicationService = jexxaMain.getInstanceOfPort(IncrementApplicationService.class);
        List<Integer> expectedResult = IntStream.rangeClosed(1, MAX_COUNTER)
                .boxed()
                .collect(toList());

        jexxaMain.start();

        //Act
        incrementService(jexxaMain.getProperties());
        
        //Assert
        jexxaMain.stop();

        assertEquals(expectedResult, incrementApplicationService.getUsedCounter());
    }

    private void incrementService(Properties properties)
    {
        MyProducer myProducer = new MyProducer(properties);
        while ( incrementApplicationService.getCounter() < MAX_COUNTER )
        {
            //Act
            myProducer.sendToTopic();
        }
    }
    

    static class MyProducer implements AutoCloseable
    {
        private final Connection connection;
        private final Session session;
        private final MessageProducer producer;

        MyProducer(Properties properties) 
        {
            JMSAdapter jmsAdapter = new JMSAdapter(properties);
            try
            {
                this.connection = jmsAdapter.createConnection();
                connection.start();

                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic(ApplicationServiceListener.class.getSimpleName());

                producer = session.createProducer(destination);
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            } catch (JMSException e)
            {
               throw new IllegalArgumentException(e);
            }
        }

        protected void sendToTopic()
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
