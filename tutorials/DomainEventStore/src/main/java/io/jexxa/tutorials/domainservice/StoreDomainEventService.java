package io.jexxa.tutorials.domainservice;

import io.jexxa.tutorials.domain.valueobject.DomainEvent;

public class StoreDomainEventService
{
    private final IDomainEventRepository iDomainEventRepository;

    public StoreDomainEventService(IDomainEventRepository iDomainEventRepository)
    {
        this.iDomainEventRepository = iDomainEventRepository;
    }

    public void add(DomainEvent domainEvent)
    {
        if (! iDomainEventRepository.isPresent(domainEvent.getUUID()))
        {
            iDomainEventRepository.add(domainEvent);
        }

    }
}
