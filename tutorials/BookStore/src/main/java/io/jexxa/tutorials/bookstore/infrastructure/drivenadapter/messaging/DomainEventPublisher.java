package io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.messaging;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domainservice.IDomainEventPublisher;

import java.util.Objects;
import java.util.Properties;

@SuppressWarnings("unused")
public class DomainEventPublisher implements IDomainEventPublisher
{
    private final MessageSender messageSender;

    public DomainEventPublisher(Properties properties)
    {
        messageSender = MessageSenderManager.getMessageSender(properties);
    }

    @Override
    public void publish(BookSoldOut domainEvent)
    {
        Objects.requireNonNull(domainEvent);
        messageSender
                .send(domainEvent)
                .toTopic("BookStoreTopic")
                .asJson();
    }
}
