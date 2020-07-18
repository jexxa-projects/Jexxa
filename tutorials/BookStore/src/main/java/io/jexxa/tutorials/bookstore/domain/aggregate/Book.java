package io.jexxa.tutorials.bookstore.domain.aggregate;

import static io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut.bookSoldOut;

import java.util.Optional;

import io.jexxa.tutorials.bookstore.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public final class Book
{
    private final ISBN13 isbn13;
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
            return Optional.of(bookSoldOut(isbn13));
        }

        return Optional.empty();
    }

    //AggregateFactory
    public static Book newBook(ISBN13 isbn13)
    {
        return new Book(isbn13);
    }

}
