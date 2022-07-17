package io.jexxa.jexxatest.architecture.validapplication.domain.domainevent;


import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.jexxatest.architecture.validapplication.domain.valueobject.ValidValueObject;

@DomainEvent
public record ValidDomainEvent(ValidValueObject validValueObject) {
}
