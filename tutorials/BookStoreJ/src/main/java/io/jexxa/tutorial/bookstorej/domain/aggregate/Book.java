package io.jexxa.tutorial.bookstorej.domain.aggregate;

import static io.jexxa.tutorial.bookstorej.domain.domainevent.BookSoldOut.bookSoldOut;

import java.util.Optional;

import io.jexxa.addend.applicationcore.Aggregate;
import io.jexxa.addend.applicationcore.AggregateFactory;
import io.jexxa.addend.applicationcore.AggregateID;
import io.jexxa.tutorial.bookstorej.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorial.bookstorej.domain.domainevent.BookSoldOut;
import io.jexxa.tutorial.bookstorej.domain.valueobject.ISBN13;

@Aggregate
public final class Book
{
    private final ISBN13 isbn13;
    private int amountInStock = 0;

    private Book(ISBN13 isbn13)
    {
        this.isbn13 = isbn13;
    }

    @AggregateID
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

    @AggregateFactory(Book.class)
    public static Book newBook(ISBN13 isbn13)
    {
        return new Book(isbn13);
    }
}
