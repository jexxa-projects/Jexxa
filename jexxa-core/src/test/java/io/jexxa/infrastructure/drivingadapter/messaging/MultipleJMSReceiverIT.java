package io.jexxa.infrastructure.drivingadapter.messaging;

import io.jexxa.TestConstants;
import io.jexxa.application.JexxaTestApplication;
import io.jexxa.application.applicationservice.IncrementApplicationService;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.utils.messaging.ITMessageSender;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@Tag(TestConstants.INTEGRATION_TEST)
class MultipleJMSReceiverIT
{
    private static final int MAX_COUNTER = 1000;
    private static final int MAX_THREADS = 5;
    private static final String MESSAGE = "Hello World";

    private static final String DESTINATION = "ApplicationServiceListener";

    private IncrementApplicationService incrementApplicationService;


    public record ApplicationServiceListener(IncrementApplicationService incrementApplicationService) implements MessageListener
    {
        @Override
        @JMSConfiguration(destination = DESTINATION, messagingType = JMSConfiguration.MessagingType.TOPIC)
        public void onMessage(Message message) {
                incrementApplicationService.increment();
            }
    }



    @Test
    void synchronizeMultipleClients()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(JexxaTestApplication.class);

        jexxaMain.disableBanner();

        incrementApplicationService = jexxaMain.getInstanceOfPort(IncrementApplicationService.class);

        for ( int i = 0; i < MAX_THREADS; ++i)
        {
            jexxaMain.bind(JMSAdapter.class).to(new ApplicationServiceListener(incrementApplicationService));
        }
        List<Integer> expectedResult = IntStream.rangeClosed(1, MAX_COUNTER)
                .boxed()
                .collect(toList());

        jexxaMain.start();

        //Act
        assertTimeout(Duration.ofSeconds(10), () -> incrementService(jexxaMain.getProperties()));

        //Assert
        jexxaMain.stop();

        assertEquals(expectedResult, incrementApplicationService.getUsedCounter());
    }

    private void incrementService(Properties properties)
    {
        try (ITMessageSender myProducer = new ITMessageSender(properties, DESTINATION, JMSConfiguration.MessagingType.TOPIC))
        {
            while (incrementApplicationService.getCounter() < MAX_COUNTER) {
                //Act
                myProducer.send(MESSAGE);
            }
        }
    }
}
