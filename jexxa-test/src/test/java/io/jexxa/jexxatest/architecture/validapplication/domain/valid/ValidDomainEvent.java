package io.jexxa.jexxatest.architecture.validapplication.domain.valid;


import io.jexxa.addend.applicationcore.DomainEvent;

@SuppressWarnings("unused")
@DomainEvent
public record ValidDomainEvent(ValidValueObject validValueObject) {
}
