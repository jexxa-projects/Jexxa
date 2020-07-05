package io.jexxa.tutorials.timeservice.infrastructure.drivenadapter.messaging;

import java.time.LocalTime;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.JMSSender;
import io.jexxa.tutorials.timeservice.domainservice.ITimePublisher;

@SuppressWarnings("unused")
public class JMSTimePublisher implements ITimePublisher
{
    private final JMSSender jmsSender;

    private static final String TIME_TOPIC = "TimeService";

    public JMSTimePublisher(Properties properties)
    {
        this.jmsSender = new JMSSender(properties);
    }

    @Override
    public void publish(LocalTime localTime)
    {
        jmsSender.sendToTopic(localTime.toString(), TIME_TOPIC);
    }
}
