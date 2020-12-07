package io.jexxa.tutorials.bookstorej.infrastructure.drivenadapter.messaging;

import java.util.Properties;

import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.tutorials.bookstorej.domainservice.IDomainEventPublisher;
import org.apache.commons.lang3.Validate;

@SuppressWarnings("unused")
@DrivenAdapter
public class DomainEventPublisher implements IDomainEventPublisher
{
    private final MessageSender messageSender;

    public DomainEventPublisher(Properties properties)
    {
        messageSender = MessageSenderManager.getMessageSender(properties);
    }

    @Override
    public <T> void publish(T domainEvent)
    {
        Validate.notNull(domainEvent);
        messageSender
                .send(domainEvent)
                .toTopic("BookStoreTopic")
                .asJson();
    }
}
