package io.jexxa.application.infrastructure.drivenadapter.messaging;

import java.time.LocalTime;
import java.util.Properties;

import io.jexxa.application.domain.valueobject.JexxaValueObject;
import io.jexxa.application.domainservice.IJexxaPublisher;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;

public class JexxaMessageSender implements IJexxaPublisher
{
    public static final String JEXXA_TOPIC = "JexxaTopic";
    public static final String JEXXA_QUEUE = "JexxaQueue";

    private final MessageSender messageSender;

    public JexxaMessageSender(Properties properties)
    {
        //Request a default message Sender from corresponding strategy manager
        this.messageSender = MessageSenderManager.getInstance().getStrategy(properties);
    }

    @Override
    public void sendToQueue(JexxaValueObject jexxaValueObject)
    {
        messageSender.send(jexxaValueObject)
                .toQueue(JEXXA_QUEUE)
                .asJson();
    }

    @Override
    public void sendToTopic(JexxaValueObject jexxaValueObject)
    {
        messageSender.send(jexxaValueObject)
                .toTopic(JEXXA_TOPIC)
                .asJson();
    }
}
