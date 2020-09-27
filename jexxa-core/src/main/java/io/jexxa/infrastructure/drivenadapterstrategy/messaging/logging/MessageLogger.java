package io.jexxa.infrastructure.drivenadapterstrategy.messaging.logging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;

public class MessageLogger extends MessageSender
{
    private static final Logger MESSAGE_LOGGER = JexxaLogger.getLogger(MessageLogger.class);

    @Override
    protected void sendMessageToQueue(String message, String destination, Properties messageProperties)
    {
        MESSAGE_LOGGER.info("Begin> Send message");
        MESSAGE_LOGGER.info("Message           : {}", message);
        MESSAGE_LOGGER.info("Destination       : {}", destination);
        MESSAGE_LOGGER.info("Destination-Type  : QUEUE");
        MESSAGE_LOGGER.info("End> Send message");
    }

    @Override
    protected void sendMessageToTopic(String message, String destination, Properties messageProperties)
    {
        MESSAGE_LOGGER.info("Begin> Send message");
        MESSAGE_LOGGER.info("Message           : {}", message);
        MESSAGE_LOGGER.info("Destination       : {}", destination);
        MESSAGE_LOGGER.info("Destination-Type  : TOPIC");
        MESSAGE_LOGGER.info("End> Send message");
    }
}
