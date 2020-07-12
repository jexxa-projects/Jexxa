package io.jexxa.tutorials.bookstore.domain.aggregate;

import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public class Book
{
    private final ISBN13 isbn13;
    private boolean outOfPrint = false;

    private Book(ISBN13 isbn13)
    {
        this.isbn13 = isbn13;
    }

    // AggregateID 
    ISBN13 getISBN13()
    {
        return isbn13;
    }

    public void outOfPrint()
    {
        outOfPrint = true;
    }

    public boolean isOutOfPrint()
    {
        return  outOfPrint;
    }

    //AggregateFactory
    public static Book create(ISBN13 isbn13)
    {
        return new Book(isbn13);
    }

}
