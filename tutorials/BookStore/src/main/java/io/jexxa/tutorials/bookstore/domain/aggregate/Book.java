package io.jexxa.tutorials.bookstore.domain.aggregate;

import java.util.Optional;

import io.jexxa.tutorials.bookstore.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookOutOfPrint;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public class Book
{
    private final ISBN13 isbn13;
    private boolean outOfPrint = false;
    private int amountInStock = 0;

    private Book(ISBN13 isbn13)
    {
        this.isbn13 = isbn13;
    }

    // AggregateID 
    public ISBN13 getISBN13()
    {
        return isbn13;
    }

    public BookOutOfPrint outOfPrint()
    {
        outOfPrint = true;
        return new BookOutOfPrint(isbn13, amountInStock);
    }

    public boolean isOutOfPrint()
    {
        return  outOfPrint;
    }

    public boolean inStock()
    {
        return amountInStock > 0;
    }

    public int amountInStock()
    {
        return amountInStock;
    }

    public void addToStock( int amount )
    {
        amountInStock += amount;
    }

    public Optional<BookSoldOut> sell() throws BookNotInStockException
    {
        if ( ! inStock() )
        {
            throw new BookNotInStockException();
        }

        amountInStock -= 1;

        if ( ! inStock() )
        {
            return Optional.of(new BookSoldOut(isbn13));
        }

        return Optional.empty();
    }

    //AggregateFactory
    public static Book create(ISBN13 isbn13)
    {
        return new Book(isbn13);
    }

}
