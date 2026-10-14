package io.jexxa.jexxatest.architecture.validapplication.domainservice;

import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidDomainEvent;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidSealedInterfaceDomainEvents;

@InfrastructureService
@SuppressWarnings("unused")
public interface IDomainEventSender {
    void sendDomainEvent(ValidDomainEvent validDomainEvent);
    void sendDomainEvent(ValidSealedInterfaceDomainEvents validDomainEvent);
}
