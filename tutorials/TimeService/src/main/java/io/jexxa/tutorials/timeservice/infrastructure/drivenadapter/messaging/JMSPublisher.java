package io.jexxa.tutorials.timeservice.infrastructure.drivenadapter.messaging;

import java.time.LocalTime;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.JMSSender;
import io.jexxa.tutorials.timeservice.domainservice.ITimePublisher;

@SuppressWarnings("unused")
public class JMSPublisher implements ITimePublisher
{
    private static final String TIME_TOPIC = "TimeService";

    private final JMSSender jmsSender;

    // For all driven adapter we have to provide either a static factory or a public constructor to
    // enable implicit constructor injection
    public JMSPublisher(Properties properties)
    {
        this.jmsSender = new JMSSender(properties);
    }

    @Override
    public void publish(LocalTime localTime)
    {
        // Send the message to the topic.
        // Important note: The JMSSender internally uses json to serialize given objects.
        // Therefore, the receiver must deserialize the received message accordingly
        jmsSender.sendToTopic(localTime, TIME_TOPIC);
    }
}
