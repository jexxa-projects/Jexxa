package io.jexxa.tutorials.timeservice.infrastructure.drivingadapter.messaging;

import static io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration.MessagingType.TOPIC;

import java.time.LocalTime;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.infrastructure.drivingadapter.messaging.listener.JSONMessageListener;
import io.jexxa.tutorials.timeservice.applicationservice.TimeService;

@SuppressWarnings("unused")
public final class PublishTimeListener extends JSONMessageListener<LocalTime>
{
    private final TimeService timeService;
    private static final String TIME_TOPIC = "TimeService";

    //To implement a so called PortAdapter we need a public constructor which expects a single argument that must be a InboundPort.
    public PublishTimeListener(TimeService timeService)
    {
        super(LocalTime.class);
        this.timeService = timeService;
    }

    @Override
    // The JMS specific configuration is defined via annotation.
    @JMSConfiguration(destination = TIME_TOPIC,  messagingType = TOPIC)
    public void onMessage(LocalTime localTime)
    {
        // Forward this information to corresponding application service.
        timeService.displayPublishedTime(localTime);
    }
}
