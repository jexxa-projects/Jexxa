package io.jexxa.infrastructure.drivenadapterstrategy.messaging.logging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;

public class MessageLogger extends MessageSender
{
    private static final Logger jexxaLogger = JexxaLogger.getLogger(MessageLogger.class);

    @Override
    protected void sendMessageToQueue(String message, String destination, Properties messageProperties)
    {
        jexxaLogger.info("Begin> Send message");
        jexxaLogger.info("Message           : {}", message);
        jexxaLogger.info("Destination       : {}", destination);
        jexxaLogger.info("Destination-Type  : QUEUE");
        jexxaLogger.info("End> Send message");
    }

    @Override
    protected void sendMessageToTopic(String message, String destination, Properties messageProperties)
    {
        jexxaLogger.info("Begin> Send message");
        jexxaLogger.info("Message           : {}", message);
        jexxaLogger.info("Destination       : {}", destination);
        jexxaLogger.info("Destination-Type  : TOPIC");
        jexxaLogger.info("End> Send message");
    }
}
