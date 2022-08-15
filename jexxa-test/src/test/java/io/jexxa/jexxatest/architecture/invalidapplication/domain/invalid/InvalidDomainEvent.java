package io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid;


import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidValueObject;

@DomainEvent
@SuppressWarnings("unused")
public record InvalidDomainEvent(ValidValueObject validValueObject) {
}
