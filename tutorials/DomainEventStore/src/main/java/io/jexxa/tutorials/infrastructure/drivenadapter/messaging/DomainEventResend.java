package io.jexxa.tutorials.infrastructure.drivenadapter.messaging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.tutorials.domain.valueobject.DomainEvent;
import io.jexxa.tutorials.domainservice.IDomainEventResend;

public class DomainEventResend implements IDomainEventResend
{
    private final MessageSender messageSender;

    public DomainEventResend(Properties properties)
    {
        messageSender = MessageSenderManager.getMessageSender(properties);
    }

    @Override
    public void resend(DomainEvent domainEvent)
    {
        messageSender.send(domainEvent)
                .toTopic("BookStoreTopic")
                .asJson();
    }
}
