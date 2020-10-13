package io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.messaging;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domainservice.IDomainEventPublisher;
import org.apache.commons.lang3.Validate;

@SuppressWarnings("unused")
public class DomainEventPublisher implements IDomainEventPublisher
{
    private final MessageSender messageSender;

    public DomainEventPublisher(Properties properties)
    {
        messageSender = MessageSenderManager.getInstance().getStrategy(properties);
    }

    @Override
    public void publish(BookSoldOut domainEvent)
    {
        Validate.notNull(domainEvent);
        messageSender
                .send(domainEvent)
                .toTopic("BookStoreTopic")
                .asJson();
    }
}
