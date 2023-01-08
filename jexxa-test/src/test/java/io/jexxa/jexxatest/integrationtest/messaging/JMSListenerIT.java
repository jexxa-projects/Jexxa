package io.jexxa.jexxatest.integrationtest.messaging;

import io.jexxa.application.domain.model.JexxaValueObject;
import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.jexxatest.JexxaIntegrationTest;
import io.jexxa.jexxatest.application.JexxaITTestApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JMSListenerIT {

    private final static JexxaIntegrationTest jexxaIntegrationTest = new JexxaIntegrationTest(JexxaITTestApplication.class);

    @Test
    void testMessageListener()
    {
        //Arrange
        var testTopic = "TestTopic";
        var messageSender = jexxaIntegrationTest.getMessageSender();
        var objectUnderTest =jexxaIntegrationTest.getMessageListener(testTopic, JMSConfiguration.MessagingType.TOPIC);

        //Act
        messageSender.send(new JexxaValueObject(42)).toTopic(testTopic).asJson();

        var result = objectUnderTest
                .waitUntilMessageReceived(5, TimeUnit.SECONDS)
                .pop(JexxaValueObject.class);

        //Assert
        assertEquals(new JexxaValueObject(42), result);
        assertTrue(objectUnderTest.getMessages().isEmpty());
    }

    @Test
    void testRegisterMessageListener()
    {
        //Arrange
        var testTopic = "TestTopic";
        var messageSender = jexxaIntegrationTest.getMessageSender();
        var objectUnderTest = new JMSListener(testTopic, JMSConfiguration.MessagingType.TOPIC);
        jexxaIntegrationTest.registerMessageListener(objectUnderTest);

        //Act
        messageSender.send(new JexxaValueObject(42)).toTopic(testTopic).asJson();

        var result = objectUnderTest
                .waitUntilMessageReceived(5, TimeUnit.SECONDS)
                .pop(JexxaValueObject.class);

        //Assert
        assertEquals(new JexxaValueObject(42), result);
        assertTrue(objectUnderTest.getMessages().isEmpty());
    }

    @Test
    void testClearMessageListener()
    {
        //Arrange
        var testTopic = "TestTopic";
        var messageSender = jexxaIntegrationTest.getMessageSender();
        var objectUnderTest =jexxaIntegrationTest.getMessageListener(testTopic, JMSConfiguration.MessagingType.TOPIC);

        //Act
        messageSender.send(new JexxaValueObject(42)).toTopic(testTopic).asJson();
        objectUnderTest
                .waitUntilMessageReceived(5, TimeUnit.SECONDS)
                .clear();

        messageSender.send(new JexxaValueObject(42)).toTopic(testTopic).asJson();
        var result = objectUnderTest
                .waitUntilMessageReceived(5, TimeUnit.SECONDS)
                .pop(JexxaValueObject.class);

        //Assert
        assertEquals(new JexxaValueObject(42), result);
        assertTrue(objectUnderTest.getMessages().isEmpty());
    }

    @AfterAll
    static void tearDown()
    {
        jexxaIntegrationTest.shutDown();
    }

}
