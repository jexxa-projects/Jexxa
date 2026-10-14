package io.jexxa.jexxatest.architecture.validapplication.domain.valid;

import io.jexxa.addend.applicationcore.DomainEvent;

import java.util.UUID;

@SuppressWarnings("unused")
@DomainEvent
public sealed interface ValidSealedInterfaceDomainEvents {
    UUID eventId();
    ValidValueObject validValueObject();

    @DomainEvent
    record InvalidSealedDomainEvent(UUID eventId, ValidValueObject validValueObject)    implements ValidSealedInterfaceDomainEvents {}
    static InvalidSealedDomainEvent validSealedDomainEvent(UUID eventId, ValidValueObject validValueObject)       { return new InvalidSealedDomainEvent(eventId, validValueObject); }
}
