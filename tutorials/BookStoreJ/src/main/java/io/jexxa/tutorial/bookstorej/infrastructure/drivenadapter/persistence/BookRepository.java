package io.jexxa.tutorial.bookstorej.infrastructure.drivenadapter.persistence;


import java.util.List;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.RepositoryManager;
import io.jexxa.tutorial.bookstorej.domain.aggregate.Book;
import io.jexxa.tutorial.bookstorej.domain.valueobject.ISBN13;
import io.jexxa.tutorial.bookstorej.domainservice.IBookRepository;

@SuppressWarnings("unused")
@DrivenAdapter
public final class BookRepository implements IBookRepository
{
    private final IRepository<Book, ISBN13> repository;

    private BookRepository(IRepository<Book, ISBN13> repository)
    {
        this.repository = repository;
    }

    // Factory method that requests a repository strategy from Jexxa's RepositoryManager
    public static IBookRepository create(Properties properties)
    {
        return new BookRepository(
                RepositoryManager.getInstance().getStrategy(Book.class, Book::getISBN13, properties)
        );
    }

    @Override
    public void add(Book book)
    {
        repository.add(book);
    }

    @Override
    public Book get(ISBN13 isbn13)
    {
        return repository.get(isbn13).orElseThrow();
    }

    @Override
    public boolean isRegistered(ISBN13 isbn13)
    {
        return search(isbn13)
                .isPresent();
    }

    @Override
    public Optional<Book> search(ISBN13 isbn13)
    {
        return repository.get(isbn13);
    }

    @Override
    public void update(Book book)
    {
        repository.update(book);
    }

    @Override
    public List<Book> getAll()
    {
        return repository.get();
    }
}
