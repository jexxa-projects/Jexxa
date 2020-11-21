package io.jexxa.tutorials.applicationservice;

import java.util.List;

import io.jexxa.tutorials.domain.valueobject.DomainEvent;
import io.jexxa.tutorials.domainservice.ISimpleDomainEventRepository;

public class SimpleDomainEventStore
{
    private final ISimpleDomainEventRepository iSimpleDomainEventRepository;

    public SimpleDomainEventStore(ISimpleDomainEventRepository iSimpleDomainEventRepository)
    {
        this.iSimpleDomainEventRepository = iSimpleDomainEventRepository;
    }

    public void addDomainEvent(DomainEvent domainEvent)
    {
        iSimpleDomainEventRepository.add(domainEvent);
    }

    public List<DomainEvent> getDomainEvents()
    {
        return iSimpleDomainEventRepository.getAll();
    }
}
