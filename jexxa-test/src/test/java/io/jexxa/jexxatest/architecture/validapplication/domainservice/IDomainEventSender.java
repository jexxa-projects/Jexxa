package io.jexxa.jexxatest.architecture.validapplication.domainservice;

import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.jexxatest.architecture.validapplication.domain.domainevent.ValidDomainEvent;

@InfrastructureService
public interface IDomainEventSender {
    void sendDomainEvent(ValidDomainEvent validDomainEvent);
}
