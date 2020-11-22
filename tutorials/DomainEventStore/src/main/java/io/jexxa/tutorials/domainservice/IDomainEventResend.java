package io.jexxa.tutorials.domainservice;

import io.jexxa.tutorials.domain.valueobject.DomainEvent;

public interface IDomainEventResend
{
    void resend(DomainEvent domainEvent);
}
