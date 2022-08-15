package io.jexxa.jexxatest.architecture.validapplication.domain.valid;


import io.jexxa.addend.applicationcore.DomainEvent;

@DomainEvent
public record ValidDomainEvent(ValidValueObject validValueObject) {
}
