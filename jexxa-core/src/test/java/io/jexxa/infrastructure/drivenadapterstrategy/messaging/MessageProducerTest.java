package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;

import com.google.gson.Gson;
import io.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.jupiter.api.Test;

class MessageProducerTest
{

    @Test
    void testMessageAsText()
    {
        //Arrange
        var localMessageSender = new LocalMessageSender();
        var testData = new JexxaValueObject(42);
        var objectUnderTest = localMessageSender.send(testData);

        //Act
        objectUnderTest.toQueue("TestQueue")
                .asString();

        //Assertions
        assertNotNull(localMessageSender.getMessage());
        assertEquals(MessageProducer.DestinationType.QUEUE, localMessageSender.getDestinationType());

        assertEquals(testData.toString(), localMessageSender.getMessage());
    }

    @Test
    void testMessageAsGson()
    {
        //Arrange
        var localMessageSender = new LocalMessageSender();
        var testData = new JexxaValueObject(42);
        var objectUnderTest = localMessageSender.send(testData);

        //Act
        objectUnderTest.toQueue("TestQueue")
                .asJson();

        //Assertions
        assertNotNull(localMessageSender.getMessage());
        assertEquals(MessageProducer.DestinationType.QUEUE, localMessageSender.getDestinationType());

        assertEquals(new Gson().toJson(testData), localMessageSender.getMessage());
    }

    @Test
    void testInvalidMessageProducerUsage()
    {
        //Arrange
        var localMessageSender = new LocalMessageSender();
        var objectUnderTest = localMessageSender.send(new Object());

        //No destination set
        assertThrows( NullPointerException.class, objectUnderTest::asString);
    }



    private static class LocalMessageSender extends MessageSender
    {
        private String message;
        private MessageProducer.DestinationType destinationType = null;

        @Override
        protected void sendMessageToQueue(String message, String destination, Properties properties)
        {
            this.message = message;
            this.destinationType = MessageProducer.DestinationType.QUEUE;
        }

        @Override
        protected void sendMessageToTopic(String message, String destination, Properties properties)
        {
            this.message = message;
            this.destinationType = MessageProducer.DestinationType.TOPIC;
        }

        String getMessage()
        {
            return message;
        }

        MessageProducer.DestinationType getDestinationType()
        {
            return destinationType;
        }

    }
}