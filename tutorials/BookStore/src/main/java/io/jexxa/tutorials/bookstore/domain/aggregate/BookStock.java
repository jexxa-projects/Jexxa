package io.jexxa.tutorials.bookstore.domain.aggregate;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

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

    static public BookStock create(BookStore bookStore)
    {
        return new BookStock(bookStore);
    }


}
