package io.jexxa.tutorials.bookstorej16.infrastructure.drivenadapter.messaging;

import java.util.Objects;
import java.util.Properties;

import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.tutorials.bookstorej16.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstorej16.domainservice.IDomainEventPublisher;

@SuppressWarnings("unused")
@DrivenAdapter
public record DomainEventPublisher(
        MessageSender messageSender) implements IDomainEventPublisher
{

    @Override
    public void publish(BookSoldOut domainEvent)
    {
        Objects.requireNonNull(domainEvent);
        messageSender
                .send(domainEvent)
                .toTopic("BookStoreTopic")
                .asJson();
    }

    // Factory method that requests a repository strategy from Jexxa's RepositoryManager
    public static IDomainEventPublisher create(Properties properties)
    {
        return new DomainEventPublisher(
                MessageSenderManager.getMessageSender(properties)
        );
    }
}
