package io.jexxa.jexxatest.architecture.invalidapplication.domain.domainevent;


import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.jexxatest.architecture.validapplication.domain.valueobject.ValidValueObject;

@DomainEvent
public record InvalidDomainEvent(ValidValueObject validValueObject) {
}
