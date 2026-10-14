package io.jexxa.jexxatest.architecture.invalidapplication.domain.invalid;

import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.jexxatest.architecture.validapplication.domain.valid.ValidValueObject;

import java.util.UUID;

@SuppressWarnings("unused")
@DomainEvent
public interface InvalidSealedInterfaceDomainEvents {
    UUID eventId();
    ValidValueObject validValueObject();

    //@DomainEvent
    record ValidSealedDomainEvent(UUID eventId, ValidValueObject validValueObject)    implements InvalidSealedInterfaceDomainEvents {}
    static ValidSealedDomainEvent validSealedDomainEvent(UUID eventId, ValidValueObject validValueObject)       { return new ValidSealedDomainEvent(eventId, validValueObject); }
}
