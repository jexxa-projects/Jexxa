package io.jexxa.jexxatest;

import io.jexxa.application.applicationservice.ApplicationServiceWithInvalidDrivenAdapters;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.domainservice.IInvalidConstructor;
import io.jexxa.application.domainservice.IJexxaAggregateRepository;
import io.jexxa.application.domainservice.IJexxaPublisher;
import io.jexxa.application.domainservice.INotImplementedService;
import io.jexxa.application.domainservice.InitializeJexxaAggregates;
import io.jexxa.application.domainservice.PublishDomainInformation;
import io.jexxa.core.JexxaMain;
import io.jexxa.core.factory.InvalidAdapterException;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class JexxaTestTest
{
    private JexxaTest jexxaTest;
    private JexxaMain jexxaMain;


    @BeforeEach
    void setUp()
    {
        //Arrange
        jexxaMain = new JexxaMain(JexxaTestTest.class.getSimpleName(), new Properties());
        jexxaMain.addToApplicationCore("io.jexxa.application.domainservice")
                .addToInfrastructure("io.jexxa.application.infrastructure");

        jexxaTest = new JexxaTest(jexxaMain);
    }

    @Test
    void invalidPort()
    {
        assertThrows(InvalidAdapterException.class, () -> jexxaTest.getInstanceOfPort(ApplicationServiceWithInvalidDrivenAdapters.class));
    }

    @Test
    void validateRepository()
    {
        //Arrange
        var jexxaRepository = jexxaTest.getRepository(IJexxaAggregateRepository.class);

        //Act
        jexxaTest.getInstanceOfPort(InitializeJexxaAggregates.class).initDomainData();

        //Assert
        assertFalse(jexxaRepository.get().isEmpty());
    }


    @Test
    void validateMessageToTopic()
    {
        //Arrange
        var testMessage = new JexxaValueObject(1);
        var messageRecorder= jexxaTest.getMessageRecorder(IJexxaPublisher.class);

        var objectUnderTest = jexxaTest.getInstanceOfPort(PublishDomainInformation.class);

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
        assertEquals(getJSONConverter().toJson(testMessage), recordedMessage.getSerializedMessage());
    }

    @Test
    void validateMessageToQueue()
    {
        //Arrange
        var testMessage = new JexxaValueObject(1);
        var messageRecorder = jexxaTest.getMessageRecorder(IJexxaPublisher.class);

        var objectUnderTest = jexxaTest.getInstanceOfPort(PublishDomainInformation.class);

        //Act
        objectUnderTest.sendToQueue(testMessage);

        //Assert MessageRecorder
        assertFalse(messageRecorder.isEmpty());
        assertEquals(1, messageRecorder.size());

        //Assert RecordedMessage
        var recordedMessage = messageRecorder.getMessage(JexxaValueObject.class);
        assertNotNull(recordedMessage);
    }

    @Test
    void repositoryNotAvailable()
    {
        //Act/Assert
        assertThrows( IllegalArgumentException.class, () -> jexxaTest.getRepository(INotImplementedService.class) );

        //Act/Assert
        assertThrows( InvalidAdapterException.class, () -> jexxaTest.getRepository(IInvalidConstructor.class) );
    }

    @Test
    void jexxaTestProperties()
    {
        //Act/Assert
        assertTrue( jexxaMain.getProperties().containsKey("io.jexxa.test.project") );
    }


}
