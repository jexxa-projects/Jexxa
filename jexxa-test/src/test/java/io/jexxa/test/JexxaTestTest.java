package io.jexxa.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import com.google.gson.Gson;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.domainservice.IJexxaAggregateRepository;
import io.jexxa.application.domainservice.IJexxaPublisher;
import io.jexxa.application.domainservice.InitializeJexxaAggregates;
import io.jexxa.application.domainservice.PublishJexxaValueObject;
import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import io.jexxa.jexxatest.JexxaTest;
import org.junit.jupiter.api.Test;


class JexxaTestTest
{

    @Test
    void validateRepository()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(JexxaTestTest.class.getSimpleName(), new Properties());
        jexxaMain.addToApplicationCore("io.jexxa.application.domainservice")
                .addToInfrastructure("io.jexxa.application.infrastructure");

        JexxaTest jexxaTest = new JexxaTest(jexxaMain);
        var jexxaRepository = jexxaTest.getRepository(IJexxaAggregateRepository.class);

        //Act
        jexxaTest.getInstanceOfPort(InitializeJexxaAggregates.class).initDomainData();

        //Assert
        assertFalse(jexxaRepository.get().isEmpty());
    }

    @Test void validateMessageToTopic()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(JexxaTestTest.class.getSimpleName(), new Properties());
        jexxaMain.addToApplicationCore("io.jexxa.application.domainservice")
                .addToInfrastructure("io.jexxa.application.infrastructure");
        JexxaTest jexxaTest = new JexxaTest(jexxaMain);

        var testMessage = new JexxaValueObject(1);
        var messageRecorder = jexxaTest.getMessageRecorder(IJexxaPublisher.class);

        var objectUnderTest = jexxaTest.getInstanceOfPort(PublishJexxaValueObject.class);

        //Act
        objectUnderTest.sendToTopic(testMessage);


        //Assert MessageRecorder
        assertFalse(messageRecorder.isEmpty());
        assertEquals(1, messageRecorder.size());

        //Assert RecordedMessage
        var tempMessage = messageRecorder.pop();
        assertTrue(tempMessage.isPresent());

        var recordedMessage = tempMessage.get();
        assertNotNull(recordedMessage);
        assertEquals("JexxaTopic", recordedMessage.getDestinationName());
        assertEquals(MessageProducer.DestinationType.TOPIC, recordedMessage.getDestinationType());
        assertNull(recordedMessage.getMessageProperties());
        assertEquals(new Gson().toJson(testMessage), recordedMessage.getSerializedMessage());
    }

    @Test void validateMessageToQueue()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(JexxaTestTest.class.getSimpleName(), new Properties());
        jexxaMain.addToApplicationCore("io.jexxa.application.domainservice")
                .addToInfrastructure("io.jexxa.application.infrastructure");
        JexxaTest jexxaTest = new JexxaTest(jexxaMain);

        var testMessage = new JexxaValueObject(1);
        var messageRecorder = jexxaTest.getMessageRecorder(IJexxaPublisher.class);

        var objectUnderTest = jexxaTest.getInstanceOfPort(PublishJexxaValueObject.class);

        //Act
        objectUnderTest.sendToQueue(testMessage);


        //Assert MessageRecorder
        assertFalse(messageRecorder.isEmpty());
        assertEquals(1, messageRecorder.size());

        //Assert RecordedMessage
        var recordedMessage = messageRecorder.getMessage(JexxaValueObject.class);
        assertNotNull(recordedMessage);
    }

}
