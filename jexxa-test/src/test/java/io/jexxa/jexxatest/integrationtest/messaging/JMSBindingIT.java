package io.jexxa.jexxatest.integrationtest.messaging;

import io.jexxa.common.drivingadapter.messaging.jms.JMSConfiguration;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.jexxatest.JexxaIntegrationTest;
import io.jexxa.jexxatest.application.JexxaITTestApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JMSBindingIT {

    private static final JexxaIntegrationTest JEXXA_INTEGRATION_TEST = new JexxaIntegrationTest(JexxaITTestApplication.class);
    private static JMSBinding objectUnderTest;

    @BeforeAll
    static void initBeforeAll()
    {
        objectUnderTest = JEXXA_INTEGRATION_TEST.getBinding(JMSBinding.class);
    }

    @Test
    void testMessageListener()
    {
        //Arrange
        var testTopic = "TestTopic";
        var messageSender = objectUnderTest.getSender();
        var messageListener = objectUnderTest.getListener(testTopic, JMSConfiguration.MessagingType.TOPIC);

        //Act
        messageSender.send(new JexxaValueObject(42)).toTopic(testTopic).asJson();

        var result = messageListener
                .awaitMessage(5, TimeUnit.SECONDS)
                .pop(JexxaValueObject.class);

        //Assert
        assertEquals(new JexxaValueObject(42), result);
        assertTrue(messageListener.getAll().isEmpty());
    }

    @Test
    void testRegisterListener()
    {
        //Arrange
        var testTopic = "TestTopic";
        var messageSender = objectUnderTest.getSender();
        var messageListener = new JMSListener(testTopic, JMSConfiguration.MessagingType.TOPIC);
        objectUnderTest.registerListener(messageListener);

        //Act
        messageSender.send(new JexxaValueObject(42)).toTopic(testTopic).asJson();

        var result = messageListener
                .awaitMessage(5, TimeUnit.SECONDS)
                .pop(JexxaValueObject.class);

        //Assert
        assertEquals(new JexxaValueObject(42), result);
        assertTrue(messageListener.getAll().isEmpty());
    }

    @Test
    void testClearMessageListener()
    {
        //Arrange
        var testTopic = "TestTopic";
        var messageSender = objectUnderTest.getSender();
        var messageListener = objectUnderTest.getListener(testTopic, JMSConfiguration.MessagingType.TOPIC);

        //Act
        messageSender.send(new JexxaValueObject(42)).toTopic(testTopic).asJson();
        messageListener
                .awaitMessage(5, TimeUnit.SECONDS)
                .clear();

        messageSender.send(new JexxaValueObject(42)).toTopic(testTopic).asJson();
        var result = messageListener
                .awaitMessage(5, TimeUnit.SECONDS)
                .pop(JexxaValueObject.class);

        //Assert
        assertEquals(new JexxaValueObject(42), result);
        assertTrue(messageListener.getAll().isEmpty());
    }

    @AfterAll
    static void tearDown()
    {
        JEXXA_INTEGRATION_TEST.shutDown();
    }
}
