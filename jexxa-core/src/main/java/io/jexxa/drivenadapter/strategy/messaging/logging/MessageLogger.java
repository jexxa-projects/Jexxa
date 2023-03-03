package io.jexxa.drivenadapter.strategy.messaging.logging;

import io.jexxa.drivenadapter.strategy.messaging.MessageSender;
import org.slf4j.Logger;

import java.util.Properties;

import static io.jexxa.api.wrapper.logger.SLF4jLogger.getLogger;

@SuppressWarnings("unused")
public class MessageLogger extends MessageSender
{
    private static final Logger MESSAGE_LOGGER = getLogger(MessageLogger.class);

    @Override
    protected void sendToQueue(String message, String destination, Properties messageProperties, MessageSender.MessageType messageType)
    {
        MESSAGE_LOGGER.info("Begin> Send message");
        MESSAGE_LOGGER.info("Message           : {}", message);
        MESSAGE_LOGGER.info("Properties        : {}", messageProperties);
        MESSAGE_LOGGER.info("Destination       : {}", destination);
        MESSAGE_LOGGER.info("Destination-Type  : QUEUE");
        MESSAGE_LOGGER.info("End> Send message");
    }

    @Override
    protected void sendToTopic(String message, String destination, Properties messageProperties, MessageSender.MessageType messageType)
    {
        MESSAGE_LOGGER.info("Begin> Send message");
        MESSAGE_LOGGER.info("Message           : {}", message);
        MESSAGE_LOGGER.info("Properties        : {}", messageProperties);
        MESSAGE_LOGGER.info("Destination       : {}", destination);
        MESSAGE_LOGGER.info("Destination-Type  : TOPIC");
        MESSAGE_LOGGER.info("End> Send message");
    }
}
