package io.jexxa.tutorials.bookstorej.infrastructure.drivenadapter.persistence;


import java.util.List;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;
import io.jexxa.tutorials.bookstorej.domain.aggregate.Book;
import io.jexxa.tutorials.bookstorej.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstorej.domainservice.IBookRepository;

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
    // Using a factory method here is just for demonstration purpose. Using public constructor would also be fine here.
    public static IBookRepository create(Properties properties)
    {
        return new BookRepository(
                RepositoryManager.getRepository(Book.class, Book::getISBN13, properties)
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
