package io.jexxa.application.domain.domainevent;

import io.jexxa.application.domain.valueobject.JexxaValueObject;

public record JexxaDomainEvent(JexxaValueObject jexxaValueObject) {

    public static JexxaDomainEvent create(JexxaValueObject jexxaValueObject) {
        return new JexxaDomainEvent(jexxaValueObject);
    }
}
