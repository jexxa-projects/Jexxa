package io.jexxa.tutorials.bookstorej.domainservice;


import io.jexxa.addend.applicationcore.InfrastructureService;

@InfrastructureService
public interface IDomainEventPublisher
{
    <T> void publish(T domainEvent);
}
