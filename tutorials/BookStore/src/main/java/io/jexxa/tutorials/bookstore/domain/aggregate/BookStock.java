package io.jexxa.tutorials.bookstore.domain.aggregate;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.jexxa.tutorials.bookstore.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookOutOfPrint;
import io.jexxa.tutorials.bookstore.domain.domainevent.BookSoldOut;
import io.jexxa.tutorials.bookstore.domain.valueobject.BookStore;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public class BookStock
{
    private final BookStore bookStore;
    private final Map<ISBN13, Map.Entry<Book, Integer> > booksInStock = new HashMap<>();

    private BookStock(BookStore bookStore)
    {
        this.bookStore = bookStore;
    }


    public void receiveBook(ISBN13 isbn13, int amount)
    {
        if ( booksInStock.containsKey(isbn13) )
        {
            var result = booksInStock.get(isbn13);
            booksInStock.put(isbn13, new AbstractMap.SimpleEntry<>(result.getKey(), amount + result.getValue()));
        }
        else
        {
            booksInStock.put(isbn13, new AbstractMap.SimpleEntry<>(Book.create(isbn13), amount));
        }
    }

    public boolean inStock(ISBN13 isbn13)
    {
        return amountInStock(isbn13) > 0;
    }

    public int amountInStock(ISBN13 isbn13)
    {
        if ( booksInStock.containsKey(isbn13) )
        {
            return booksInStock.get(isbn13).getValue();
        }

        return 0;
    }

    public BookOutOfPrint outOfPrint(ISBN13 book)
    {
        var result = booksInStock.get(book);

        if ( result == null )
        {
            return new BookOutOfPrint(bookStore, book, 0);
        }

        result.getKey().outOfPrint();
        return new BookOutOfPrint(bookStore, book, result.getValue());
    }

    public Optional<BookSoldOut> sell(ISBN13 book) throws BookNotInStockException
    {
        if ( !inStock(book) )
        {
            throw new BookNotInStockException();
        }

        var result = booksInStock.get(book);
        booksInStock.put(book, new AbstractMap.SimpleEntry<>(result.getKey(), result.getValue() - 1));

        if (amountInStock(book) == 0)
        {
            //If we sold last book => remove it 
            if (result.getKey().isOutOfPrint())
            {
                booksInStock.remove(book);
            }
            return Optional.of(new BookSoldOut(bookStore, book));
        }

        return Optional.empty();
    }

    public static BookStock create(BookStore bookStore)
    {
        return new BookStock(bookStore);
    }


}
