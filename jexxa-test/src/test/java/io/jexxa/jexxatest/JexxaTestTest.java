package io.jexxa.jexxatest;

import io.jexxa.application.JexxaTestApplication;
import io.jexxa.application.applicationservice.ApplicationServiceWithInvalidDrivenAdapters;
import io.jexxa.application.domain.model.JexxaAggregateRepository;
import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.application.domainservice.BootstrapJexxaAggregates;
import io.jexxa.application.domainservice.InvalidConstructorParameterService;
import io.jexxa.application.domainservice.NotImplementedService;
import io.jexxa.application.domainservice.ValidDomainSender;
import io.jexxa.application.infrastructure.drivenadapter.persistence.JexxaAggregateRepositoryImpl;
import io.jexxa.core.factory.InvalidAdapterException;
import io.jexxa.pattern.messaging.MessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.jexxatest.JexxaTest.getJexxaTest;
import static io.jexxa.common.wrapper.json.JSONManager.getJSONConverter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SuppressWarnings("ResultOfMethodCallIgnored")
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
        assertEquals(MessageProducer.DestinationType.TOPIC, recordedMessage.destinationType());
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
