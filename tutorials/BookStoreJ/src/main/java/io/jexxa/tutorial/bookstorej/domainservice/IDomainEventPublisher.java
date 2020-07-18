package io.jexxa.tutorial.bookstorej.domainservice;


import io.jexxa.tutorial.bookstorej.domain.domainevent.BookSoldOut;

public interface IDomainEventPublisher
{
    void publish(BookSoldOut domainEvent);
}
