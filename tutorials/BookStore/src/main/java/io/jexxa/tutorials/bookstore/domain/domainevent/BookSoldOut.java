package io.jexxa.tutorials.bookstore.domain.domainevent;

import java.util.Objects;

import io.jexxa.tutorials.bookstore.domain.valueobject.BookStore;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public class BookSoldOut
{
    private final BookStore bookStore;
    private final ISBN13 isbn13;

    public BookSoldOut(BookStore bookStore, ISBN13 isbn13)
    {
        this.bookStore = bookStore;
        this.isbn13 = isbn13;
    }

    public ISBN13 getISBN13()
    {
        return isbn13;
    }

    public BookStore getBookStore()
    {
        return bookStore;
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
        BookSoldOut that = (BookSoldOut) o;
        return Objects.equals(bookStore, that.bookStore) &&
                Objects.equals(isbn13, that.isbn13);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(bookStore, isbn13);
    }
}
