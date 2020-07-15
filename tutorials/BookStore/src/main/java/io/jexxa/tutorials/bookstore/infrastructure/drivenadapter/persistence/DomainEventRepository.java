package io.jexxa.tutorials.bookstore.infrastructure.drivenadapter.persistence;

import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstore.domainservice.IDomainEventPublisher;

@SuppressWarnings("unused")
public class DomainEventRepository implements IDomainEventPublisher
{
    private final IRepository<BookSoldOut, ISBN13> repository;

    public DomainEventRepository(Properties properties)
    {
        repository = RepositoryManager.getInstance()
                .getStrategy(BookSoldOut.class, BookSoldOut::getISBN13, properties);
    }

    @Override
    public void publish(BookSoldOut domainEvent)
    {
        repository.add(domainEvent);
    }
}
