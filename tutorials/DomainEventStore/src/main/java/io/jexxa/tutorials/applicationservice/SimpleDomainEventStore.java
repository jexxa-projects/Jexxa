package io.jexxa.tutorials.applicationservice;

import java.util.List;

import io.jexxa.tutorials.domain.valueobject.DomainEvent;
import io.jexxa.tutorials.domainservice.IDomainEventResend;
import io.jexxa.tutorials.domainservice.IDomainEventRepository;

@SuppressWarnings("unused")
public class SimpleDomainEventStore
{
    private final IDomainEventRepository iDomainEventRepository;
    private final IDomainEventResend iDomainEventResend;

    public SimpleDomainEventStore(IDomainEventRepository iDomainEventRepository, IDomainEventResend iDomainEventResend)
    {
        this.iDomainEventRepository = iDomainEventRepository;
        this.iDomainEventResend = iDomainEventResend;
    }

    public List<DomainEvent> getDomainEvents()
    {
        return iDomainEventRepository.getAll();
    }

    public void resendDomainEvent(String uuid)
    {
        var domainEvent = iDomainEventRepository.get(uuid);
        iDomainEventResend.resend(domainEvent);
    }

}
