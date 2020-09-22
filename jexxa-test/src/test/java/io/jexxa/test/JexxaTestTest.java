package io.jexxa.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import io.jexxa.core.JexxaMain;
import io.jexxa.test.messaging.MessageRecorder;
import io.jexxa.tutorials.bookstorej.domainservice.IBookRepository;
import io.jexxa.tutorials.bookstorej.domainservice.ReferenceLibrary;
import io.jexxa.tutorials.timeservice.TimeServiceApplication;
import io.jexxa.tutorials.timeservice.applicationservice.TimeService;
import io.jexxa.tutorials.timeservice.infrastructure.drivenadapter.messaging.JMSPublisher;
import org.junit.jupiter.api.Test;

class JexxaTestTest
{

    @Test
    public void validateRepository()
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
    public void validateMessages()
    {
        //Arrange
        String jmsDrivenAdapter      = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter.messaging";
        String displayDrivenAdapter  = TimeServiceApplication.class.getPackageName() + ".infrastructure.drivenadapter.display";

        JexxaMain jexxaMain = new JexxaMain(JexxaTestTest.class.getSimpleName(), new Properties());
        jexxaMain.addToInfrastructure(jmsDrivenAdapter)
                .addToInfrastructure(displayDrivenAdapter);

        JexxaTest jexxaTest = new JexxaTest(jexxaMain);

        //TODO: At the moment we need to know the real implementation => change it to interface (ITimePublisher in this case)
        MessageRecorder messageRecorder = jexxaTest.getMessageRecorder(JMSPublisher.class);

        TimeService objectUnderTest = jexxaTest.getInstanceOfPort(TimeService.class);

        //Act
        objectUnderTest.publishTime();


        //Assert
        assertNotNull(messageRecorder);
        assertFalse(messageRecorder.getMessages().isEmpty());
    }

}
