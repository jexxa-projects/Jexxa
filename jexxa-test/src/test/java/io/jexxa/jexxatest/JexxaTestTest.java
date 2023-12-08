package io.jexxa.jexxatest;

import io.jexxa.common.drivenadapter.messaging.DestinationType;
import io.jexxa.testapplication.JexxaTestApplication;
import io.jexxa.testapplication.applicationservice.ApplicationServiceWithInvalidDrivenAdapters;
import io.jexxa.testapplication.domain.model.JexxaAggregateRepository;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.testapplication.domainservice.BootstrapJexxaAggregates;
import io.jexxa.testapplication.domainservice.InvalidConstructorParameterService;
import io.jexxa.testapplication.domainservice.NotImplementedService;
import io.jexxa.testapplication.domainservice.ValidDomainSender;
import io.jexxa.testapplication.infrastructure.drivenadapter.persistence.JexxaAggregateRepositoryImpl;
import io.jexxa.core.factory.InvalidAdapterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.common.facade.json.JSONManager.getJSONConverter;
import static io.jexxa.jexxatest.JexxaTest.getJexxaTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class JexxaTestTest
{
    private JexxaTest jexxaTest;

    @BeforeEach
    void setUp()
    {
        //Arrange
        jexxaTest = getJexxaTest(JexxaTestApplication.class);
    }

    @Test
    void requestInvalidPort()
    {
        assertThrows(InvalidAdapterException.class, () -> jexxaTest.getInstanceOfPort(ApplicationServiceWithInvalidDrivenAdapters.class));
    }

    @Test
    void validateRepository()
    {
        //Arrange
        var jexxaRepository = jexxaTest.getRepository(JexxaAggregateRepository.class);

        //Act
        jexxaTest.getInstanceOfPort(BootstrapJexxaAggregates.class).initDomainData();

        //Assert
        assertFalse(jexxaRepository.get().isEmpty());
    }

    @Test
    void validateRepositoryIsInterface()
    {
        //Act/Assert
        assertThrows(IllegalArgumentException.class, () -> jexxaTest.getRepository(JexxaAggregateRepositoryImpl.class));
    }


    @Test
    void recordMessageToTopic()
    {
        //Arrange
        var testMessage = new JexxaValueObject(1);
        var messageRecorder= jexxaTest.getMessageRecorder(ValidDomainSender.class);

        var objectUnderTest = jexxaTest.getInstanceOfPort(ValidDomainSender.class);

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
        assertEquals("JexxaTopic", recordedMessage.destinationName());
        assertEquals(DestinationType.TOPIC, recordedMessage.destinationType());
        assertNull(recordedMessage.messageProperties());
        assertEquals(getJSONConverter().toJson(testMessage), recordedMessage.serializedMessage());
    }

    @Test
    void recordMessageToQueue()
    {
        //Arrange
        var testMessage = new JexxaValueObject(1);
        var messageRecorder = jexxaTest.getMessageRecorder(ValidDomainSender.class);

        var objectUnderTest = jexxaTest.getInstanceOfPort(ValidDomainSender.class);

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
        assertThrows( IllegalArgumentException.class, () -> jexxaTest.getRepository(NotImplementedService.class) );

        //Act/Assert
        assertThrows( InvalidAdapterException.class, () -> jexxaTest.getRepository(InvalidConstructorParameterService.class) );
    }

    @Test
    void jexxaTestProperties()
    {
        //Act/Assert
        assertTrue( jexxaTest.getProperties().containsKey("io.jexxa.test.project") );
    }


}
