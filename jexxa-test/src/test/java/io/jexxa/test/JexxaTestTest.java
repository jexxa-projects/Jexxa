package io.jexxa.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.Properties;

import io.jexxa.core.JexxaMain;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageProducer;
import io.jexxa.test.messaging.MessageRecorder;
import io.jexxa.tutorials.bookstorej.domainservice.IBookRepository;
import io.jexxa.tutorials.bookstorej.domainservice.ReferenceLibrary;
import io.jexxa.tutorials.timeservice.TimeServiceApplication;
import io.jexxa.tutorials.timeservice.applicationservice.TimeService;
import io.jexxa.tutorials.timeservice.domainservice.ITimePublisher;
import org.junit.jupiter.api.Test;

class JexxaTestTest
{

    @Test
    void validateRepository()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(JexxaTestTest.class.getSimpleName(), new Properties());
        JexxaTest jexxaTest = new JexxaTest(jexxaMain);
        IBookRepository bookRepository = jexxaTest.getRepository(IBookRepository.class);

        //Act
        jexxaMain.bootstrap(ReferenceLibrary.class).with(ReferenceLibrary::addLatestBooks);

        //Assert
        assertTrue(bookRepository.getAll().size() > 0);
    }


    @Test
    void validateMessages()
    {
        //Arrange
        String jmsDrivenAdapter      = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter.messaging";
        String displayDrivenAdapter  = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter.display";

        JexxaMain jexxaMain = new JexxaMain(JexxaTestTest.class.getSimpleName(), new Properties());
        JexxaTest jexxaTest = new JexxaTest(jexxaMain);

        jexxaMain.addToInfrastructure(jmsDrivenAdapter)
                .addToInfrastructure(displayDrivenAdapter);

        TimeService objectUnderTest = jexxaTest.getInstanceOfPort(TimeService.class);
        MessageRecorder messageRecorder = jexxaTest.getMessageRecorder(ITimePublisher.class);

        //Act
        objectUnderTest.publishTime();


        //Assert
        assertFalse(messageRecorder.getMessages().isEmpty());

        var recordedMessage = messageRecorder.pop();
        assertNotNull(recordedMessage);
        assertNotNull(recordedMessage.getMessage(LocalTime.class));
        assertEquals("TimeService", recordedMessage.getDestinationName());
        assertEquals(MessageProducer.DestinationType.TOPIC, recordedMessage.getDestinationType());
        assertNull(recordedMessage.getMessageProperties());
        assertTrue(recordedMessage.getSerializedMessage().contains("hour"));
    }

}
