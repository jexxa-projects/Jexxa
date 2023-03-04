package io.jexxa.testapplication.domainservice;

import io.jexxa.testapplication.domain.model.JexxaValueObject;

public interface ValidDomainSender
{
    void sendToQueue(JexxaValueObject jexxaValueObject);

    void sendToTopic(JexxaValueObject jexxaValueObject);
}
