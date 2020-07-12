package io.jexxa.tutorials.bookstore.domain.domainevent;

import java.util.Objects;

import io.jexxa.tutorials.bookstore.domain.valueobject.BookStore;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public class BookOutOfPrint
{
    private final BookStore bookStore;
    private final ISBN13 isbn13;
    private final int availableCopies;

    public BookOutOfPrint(BookStore bookStore, ISBN13 isbn13, int availableCopies)
    {
        this.bookStore = bookStore;
        this.isbn13 = isbn13;
        this.availableCopies = availableCopies;
    }

    public int avaliableCopies()
    {
        return availableCopies;
    }

    public BookStore getBookStore()
    {
        return bookStore;
    }

    public ISBN13 getISBN13()
    {
        return isbn13;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        BookOutOfPrint that = (BookOutOfPrint) o;
        return availableCopies == that.availableCopies &&
                Objects.equals(bookStore, that.bookStore) &&
                Objects.equals(isbn13, that.isbn13);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(bookStore, isbn13, availableCopies);
    }
}
