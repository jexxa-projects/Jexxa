package io.jexxa.tutorials.timeservice.infrastructure.drivenadapter.messaging;

import java.time.LocalTime;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.JMSSender;
import io.jexxa.tutorials.timeservice.domainservice.ITimePublisher;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;

@SuppressWarnings("unused")
public class JMSTimePublisher implements ITimePublisher
{
    private static final String TIME_TOPIC = "TimeService";

    private static final Logger LOGGER = JexxaLogger.getLogger(JMSTimePublisher.class);

    private final JMSSender jmsSender;
    
    public JMSTimePublisher(Properties properties)
    {
        this.jmsSender = new JMSSender(properties);
    }

    @Override
    public void publish(LocalTime localTime)
    {
        var localTimeAsString = localTime.toString();
        jmsSender.sendToTopic(localTimeAsString, TIME_TOPIC);
        LOGGER.info("Successfully published time {} to topic {}", localTimeAsString, TIME_TOPIC);
    }
}
