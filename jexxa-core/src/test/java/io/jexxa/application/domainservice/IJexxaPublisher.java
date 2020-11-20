package io.jexxa.application.domainservice;

import io.jexxa.application.domain.domainevent.JexxaDomainEvent;
import io.jexxa.application.domain.valueobject.JexxaValueObject;

public interface IJexxaPublisher
{
    void sendToQueue(JexxaValueObject jexxaValueObject);

    void sendToTopic(JexxaValueObject jexxaValueObject);

    void sendDomainEvent(JexxaDomainEvent jexxaDomainEvent);
}
