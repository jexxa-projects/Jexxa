package io.jexxa.testapplication.infrastructure.drivenadapter.messaging;

import io.jexxa.common.drivenadapter.messaging.MessageSender;
import io.jexxa.testapplication.domain.model.JexxaValueObject;
import io.jexxa.testapplication.domainservice.ValidDomainSender;

import java.util.Properties;

import static io.jexxa.common.drivenadapter.messaging.MessageSenderFactory.createMessageSender;

@SuppressWarnings("unused")
public class ValidDomainSenderImpl implements ValidDomainSender
{
    public static final String JEXXA_TOPIC = "JexxaTopic";
    public static final String JEXXA_QUEUE = "JexxaQueue";

    private final MessageSender messageSender;

    public ValidDomainSenderImpl(Properties properties)
    {
        this.messageSender = createMessageSender(ValidDomainSenderImpl.class, properties);
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
