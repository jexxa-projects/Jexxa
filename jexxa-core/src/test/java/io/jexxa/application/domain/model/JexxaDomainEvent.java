package io.jexxa.application.domain.model;

public record JexxaDomainEvent(JexxaValueObject jexxaValueObject) {

    public static JexxaDomainEvent create(JexxaValueObject jexxaValueObject) {
        return new JexxaDomainEvent(jexxaValueObject);
    }
}
