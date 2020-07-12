package io.jexxa.tutorials.bookstore.domainservice;

import io.jexxa.tutorials.bookstore.domain.domainevent.BookOutOfPrint;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;

public interface IDomainEventPublisher
{
    void publish(BookOutOfPrint domainEvent);

    void publish(BookSoldOut domainEvent);
}
