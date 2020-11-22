package io.jexxa.tutorials.applicationservice;

import java.util.List;

import io.jexxa.tutorials.domain.valueobject.DomainEvent;
import io.jexxa.tutorials.domainservice.IDomainEventResend;
import io.jexxa.tutorials.domainservice.ISimpleDomainEventRepository;

@SuppressWarnings("unused")
public class SimpleDomainEventStore
{
    private final ISimpleDomainEventRepository iSimpleDomainEventRepository;
    private final IDomainEventResend iDomainEventResend;

    public SimpleDomainEventStore(ISimpleDomainEventRepository iSimpleDomainEventRepository, IDomainEventResend iDomainEventResend)
    {
        this.iSimpleDomainEventRepository = iSimpleDomainEventRepository;
        this.iDomainEventResend = iDomainEventResend;
    }

    public void addDomainEvent(DomainEvent domainEvent)
    {
        if (! iSimpleDomainEventRepository.isPresent(domainEvent.getId()))
        {
            iSimpleDomainEventRepository.add(domainEvent);
        }
    }

    public List<DomainEvent> getDomainEvents()
    {
        return iSimpleDomainEventRepository.getAll();
    }

    public void resendDomainEvent(String uuid)
    {
        var domainEvent = iSimpleDomainEventRepository.get(uuid);
        iDomainEventResend.resend(domainEvent);
    }

}
