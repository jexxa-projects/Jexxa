package io.jexxa.tutorials.bookstore.domainservice;

import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;

public interface IDomainEventPublisher
{
    void publish(BookSoldOut domainEvent);
}
