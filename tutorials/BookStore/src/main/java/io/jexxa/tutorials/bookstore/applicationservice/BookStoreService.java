package io.jexxa.tutorials.bookstore.applicationservice;

import io.jexxa.tutorials.bookstore.domain.aggregate.Book;
import io.jexxa.tutorials.bookstore.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstore.domain.businessexception.InvalidISBNException;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstore.domainservice.IBookRepository;
import io.jexxa.tutorials.bookstore.domainservice.IDomainEventPublisher;
import io.jexxa.tutorials.bookstore.domainservice.ISBNService;
import org.apache.commons.lang3.Validate;

public class BookStoreService
{

    private final IBookRepository ibookRepository;
    private final IDomainEventPublisher domainEventPublisher;

    public BookStoreService(IBookRepository ibookRepository, IDomainEventPublisher domainEventPublisher)
    {
        Validate.notNull(ibookRepository);
        Validate.notNull(domainEventPublisher);

        this.ibookRepository = ibookRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    public void receiveBook(String isbn13, int amount) throws InvalidISBNException
    {
        receiveBook(ISBNService.convertFrom(isbn13), amount);
    }

    public void receiveBook(ISBN13 isbn13, int amount)
    {
        var result = ibookRepository.search( isbn13 );
        if ( result.isEmpty() )
        {
            System.out.println("CREATER BOOK ");
            ibookRepository.add(Book.create( isbn13 ));
        }

        var book = ibookRepository.get(isbn13);
        book.addToStock( amount );
        ibookRepository.update( book );
    }

    public boolean inStock(String isbn13) throws InvalidISBNException
    {
        return inStock(ISBNService.convertFrom(isbn13));
    }

    public boolean inStock(ISBN13 isbn13)
    {
        return ibookRepository
                .search( isbn13 )
                .map( Book::inStock )
                .orElse( false );
    }

    public int amountInStock(String isbn13) throws InvalidISBNException
    {
        return amountInStock(ISBNService.convertFrom(isbn13));
    }

    public int amountInStock(ISBN13 isbn13)
    {
        /*return ibookRepository
                .search(isbn13)
                .map(Book::amountInStock)
                .orElse(0);*/
        return ibookRepository
                .get(isbn13)
                .amountInStock();

    }

    public void outOfPrint(ISBN13 isbn13)
    {
        ibookRepository
                .search(isbn13)
                .map(Book::outOfPrint)
                .ifPresent(domainEventPublisher::publish);
    }

    public void sell(String isbn13) throws BookNotInStockException, InvalidISBNException
    {
        sell(ISBNService.convertFrom(isbn13));
    }

    public void sell(ISBN13 isbn13) throws BookNotInStockException
    {
        var book = ibookRepository
                .search(isbn13)
                .orElseThrow(BookNotInStockException::new);

        book.sell()
                .ifPresent(domainEventPublisher::publish);

        ibookRepository.update(book);
    }

}
