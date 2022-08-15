package io.jexxa.jexxatest.architecture.validapplication.domainservice;

import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidDomainEvent;

@InfrastructureService
@SuppressWarnings("unused")
public interface IDomainEventSender {
    void sendDomainEvent(ValidDomainEvent validDomainEvent);
}
