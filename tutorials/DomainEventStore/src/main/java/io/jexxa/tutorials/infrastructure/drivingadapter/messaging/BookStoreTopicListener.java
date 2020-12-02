package io.jexxa.tutorials.infrastructure.drivingadapter.messaging;

import io.jexxa.infrastructure.drivingadapter.messaging.JMSConfiguration;
import io.jexxa.infrastructure.drivingadapter.messaging.listener.JSONMessageListener;
import io.jexxa.tutorials.domain.valueobject.DomainEvent;
import io.jexxa.tutorials.domainservice.StoreDomainEventService;

public class BookStoreTopicListener extends JSONMessageListener<DomainEvent>
{
    private final StoreDomainEventService storeDomainEventService;

    public BookStoreTopicListener(StoreDomainEventService storeDomainEventService)
    {
        super(DomainEvent.class);
        this.storeDomainEventService = storeDomainEventService;
    }

    @Override
    @JMSConfiguration(messagingType = JMSConfiguration.MessagingType.TOPIC, destination = "BookStoreTopic")
    public void onMessage(DomainEvent domainEvent)
    {
        storeDomainEventService.add(domainEvent);
    }
}
