package io.jexxa.tutorials.bookstorej16.infrastructure.drivenadapter.persistence;

import io.jexxa.addend.infrastructure.DrivenAdapter;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;
import io.jexxa.tutorials.bookstorej16.domain.aggregate.Book;
import io.jexxa.tutorials.bookstorej16.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstorej16.domainservice.IBookRepository;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

@SuppressWarnings("unused")
@DrivenAdapter
public class BookRepository implements IBookRepository
{
    private final IRepository<Book, ISBN13> repository;

    public BookRepository (Properties properties)
    {
        this.repository = RepositoryManager.getRepository(Book.class, Book::getISBN13, properties);
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
