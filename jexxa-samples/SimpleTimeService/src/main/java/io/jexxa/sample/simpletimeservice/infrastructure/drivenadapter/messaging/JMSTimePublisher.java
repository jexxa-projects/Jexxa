package io.jexxa.sample.simpletimeservice.infrastructure.drivenadapter.messaging;

import java.time.LocalTime;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.JMSSender;
import io.jexxa.sample.simpletimeservice.domainservice.ITimePublisher;

public class JMSTimePublisher implements ITimePublisher
{
    private final JMSSender jmsSender;

    private static final String TIME_TOPIC = "TimeService";

    JMSTimePublisher(JMSSender jmsSender)
    {
        this.jmsSender = jmsSender;
    }

    @Override
    public void publish(LocalTime localTime)
    {
        jmsSender.sendToTopic(localTime.toString(), TIME_TOPIC);
    }

    public static ITimePublisher create(Properties properties)
    {
        return new JMSTimePublisher(new JMSSender(properties));
    }

}
