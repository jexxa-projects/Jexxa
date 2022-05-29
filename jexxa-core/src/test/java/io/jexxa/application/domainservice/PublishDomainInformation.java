package io.jexxa.application.domainservice;

import io.jexxa.application.domain.valueobject.JexxaValueObject;

public record PublishDomainInformation(IJexxaPublisher jexxaPublisher) {

    public void sendToQueue(JexxaValueObject jexxaValueObject) {
        jexxaPublisher.sendToQueue(jexxaValueObject);
    }

    public void sendToTopic(JexxaValueObject jexxaValueObject) {
        jexxaPublisher.sendToTopic(jexxaValueObject);
    }

}
