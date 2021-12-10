package io.jexxa.tutorials.bookstorej.applicationservice;

import io.jexxa.addend.applicationcore.ApplicationService;
import io.jexxa.tutorials.bookstorej.domain.aggregate.Book;
import io.jexxa.tutorials.bookstorej.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstorej.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstorej.domainservice.IBookRepository;
import io.jexxa.tutorials.bookstorej.domainservice.IDomainEventPublisher;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@ApplicationService
public class BookStoreService
{

    private final IBookRepository ibookRepository;
    private final IDomainEventPublisher domainEventPublisher;

    public BookStoreService(IBookRepository ibookRepository, IDomainEventPublisher domainEventPublisher)
    {
        Objects.requireNonNull(ibookRepository);
        Objects.requireNonNull(domainEventPublisher);

        this.ibookRepository = ibookRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    public void addToStock(String isbn13, int amount)
    {
        var validatedISBN = new ISBN13(isbn13);

        var result = ibookRepository.search( validatedISBN );
        if ( result.isEmpty() )
        {
            ibookRepository.add(Book.newBook( validatedISBN ));
        }

        var book = ibookRepository.get(validatedISBN);

        book.addToStock(amount);

        ibookRepository.update( book );
    }


    public boolean inStock(String isbn13)
    {
        return inStock(new ISBN13(isbn13));
    }

    boolean inStock(ISBN13 isbn13)
    {
        return ibookRepository
                .search( isbn13 )
                .map( Book::inStock )
                .orElse( false );
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
