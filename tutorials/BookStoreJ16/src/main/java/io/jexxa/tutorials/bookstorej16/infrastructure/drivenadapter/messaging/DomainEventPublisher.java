package io.jexxa.tutorials.bookstorej16.infrastructure.drivenadapter.messaging;

import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSender;
import io.jexxa.infrastructure.drivenadapterstrategy.messaging.MessageSenderManager;
import io.jexxa.tutorials.bookstorej16.domainservice.IDomainEventPublisher;

import java.util.Objects;
import java.util.Properties;

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
    public void publish(Object domainEvent)
    {
        validateDomainEvent(domainEvent);
        messageSender
                .send(domainEvent)
                .toTopic("BookStoreTopic")
                .asJson();
    }

    private void validateDomainEvent(Object domainEvent)
    {
        Objects.requireNonNull(domainEvent);
        if ( domainEvent.getClass().getAnnotation(DomainEvent.class) == null )
        {
            throw new IllegalArgumentException("Given object is not annotated with @DomainEvent");
        }
    }

}
