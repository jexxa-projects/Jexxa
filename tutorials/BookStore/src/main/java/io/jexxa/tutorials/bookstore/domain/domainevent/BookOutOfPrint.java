package io.jexxa.tutorials.bookstore.domain.domainevent;

import java.util.Objects;

import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public class BookOutOfPrint
{
    private final ISBN13 isbn13;
    private final int availableCopies;

    public BookOutOfPrint(ISBN13 isbn13, int availableCopies)
    {
        this.isbn13 = isbn13;
        this.availableCopies = availableCopies;
    }

    public int avaliableCopies()
    {
        return availableCopies;
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
                isbn13.equals(that.isbn13);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(isbn13, availableCopies);
    }
}
