package io.jexxa.tutorials.bookstorej16.domainservice;

import io.jexxa.addend.applicationcore.InfrastructureService;

@InfrastructureService
public interface IDomainEventPublisher
{
    void publish(Object domainEvent);
}
