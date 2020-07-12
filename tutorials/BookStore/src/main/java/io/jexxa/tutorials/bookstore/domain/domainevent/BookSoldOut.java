package io.jexxa.tutorials.bookstore.domain.domainevent;

import java.util.Objects;

import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public class BookSoldOut
{
    private final ISBN13 isbn13;

    public BookSoldOut(ISBN13 isbn13)
    {
        this.isbn13 = isbn13;
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
        BookSoldOut that = (BookSoldOut) o;
        return isbn13.equals(that.isbn13);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(isbn13);
    }
}
