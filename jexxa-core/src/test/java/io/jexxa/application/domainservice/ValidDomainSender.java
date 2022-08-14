package io.jexxa.application.domainservice;

import io.jexxa.application.domain.model.JexxaValueObject;

public interface ValidDomainSender
{
    void sendToQueue(JexxaValueObject jexxaValueObject);

    void sendToTopic(JexxaValueObject jexxaValueObject);
}
