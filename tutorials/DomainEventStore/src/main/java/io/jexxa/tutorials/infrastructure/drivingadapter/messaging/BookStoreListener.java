package io.jexxa.tutorials.infrastructure.drivingadapter.messaging;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.infrastructure.drivingadapter.messaging.listener.TypedMessageListener;
import io.jexxa.tutorials.applicationservice.SimpleDomainEventStore;
import io.jexxa.tutorials.domain.valueobject.DomainEvent;

public class BookStoreListener extends TypedMessageListener<DomainEvent>
{
    private final SimpleDomainEventStore simpleDomainEventStore;

    public BookStoreListener(SimpleDomainEventStore simpleDomainEventStore)
    {
        super(DomainEvent.class);
        this.simpleDomainEventStore = simpleDomainEventStore;
    }

    @Override
    @JMSConfiguration(messagingType = JMSConfiguration.MessagingType.TOPIC, destination = "BookStoreTopic")
    public void onMessage(DomainEvent domainEvent)
    {
        simpleDomainEventStore.addDomainEvent(domainEvent);
    }
}
