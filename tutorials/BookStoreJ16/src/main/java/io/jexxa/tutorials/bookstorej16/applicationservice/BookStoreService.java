package io.jexxa.tutorials.bookstorej16.applicationservice;

import static io.jexxa.tutorials.bookstorej16.domain.aggregate.Book.newBook;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.jexxa.tutorials.bookstorej16.domain.aggregate.Book;
import io.jexxa.tutorials.bookstorej16.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstorej16.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstorej16.domainservice.IBookRepository;
import io.jexxa.tutorials.bookstorej16.domainservice.IDomainEventPublisher;

@SuppressWarnings("unused")
public record BookStoreService(IBookRepository ibookRepository,
                               IDomainEventPublisher domainEventPublisher)
{
    public BookStoreService
    {
        Objects.requireNonNull(ibookRepository);
        Objects.requireNonNull(domainEventPublisher);
    }

    public void addToStock(String isbn13, int amount)
    {
        var validatedISBN = new ISBN13(isbn13);

        var result = ibookRepository.search(validatedISBN);
        if (result.isEmpty())
        {
            ibookRepository.add(newBook(validatedISBN));
        }

        var book = ibookRepository.get(validatedISBN);

        book.addToStock(amount);

        ibookRepository.update(book);
    }


    public boolean inStock(String isbn13)
    {
        return inStock(new ISBN13(isbn13));
    }

    boolean inStock(ISBN13 isbn13)
    {
        return ibookRepository
                .search(isbn13)
                .map(Book::inStock)
                .orElse(false);
    }

    public int amountInStock(String isbn13)
    {
        return amountInStock(new ISBN13(isbn13));
    }

    int amountInStock(ISBN13 isbn13)
    {
        return ibookRepository
                .search(isbn13)
                .map(Book::amountInStock)
                .orElse(0);
    }

    public void sell(String isbn13) throws BookNotInStockException
    {
        sell(new ISBN13(isbn13));
    }

    void sell(ISBN13 isbn13) throws BookNotInStockException
    {
        var book = ibookRepository
                .search(isbn13)
                .orElseThrow(BookNotInStockException::new);

        var lastBookSold = book.sell();
        lastBookSold.ifPresent(domainEventPublisher::publish);

        ibookRepository.update(book);
    }

    public List<ISBN13> getBooks()
    {
        return ibookRepository
                .getAll()
                .stream()
                .map(Book::getISBN13)
                .collect(Collectors.toList());
    }

}
