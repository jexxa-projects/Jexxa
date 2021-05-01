package io.jexxa.tutorials.bookstorej16.domainservice;

import io.jexxa.tutorials.bookstorej16.domain.domainevent.BookSoldOut;

public interface IDomainEventPublisher
{
    void publish(BookSoldOut domainEvent);
}
