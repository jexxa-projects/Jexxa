package io.jexxa.application.domainservice;

import io.jexxa.application.domain.model.JexxaValueObject;

public interface IJexxaPublisher
{
    void sendToQueue(JexxaValueObject jexxaValueObject);

    void sendToTopic(JexxaValueObject jexxaValueObject);
}
