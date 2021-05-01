package io.jexxa.tutorials.bookstorej16.domainservice;

import io.jexxa.addend.applicationcore.InfrastructureService;
import io.jexxa.tutorials.bookstorej16.domain.domainevent.BookSoldOut;

@InfrastructureService
public interface IDomainEventPublisher
{
    void publish(BookSoldOut domainEvent);
}
