package io.jexxa.tutorials.bookstore.domain.domainevent;

import java.util.Objects;

import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstore.domain.valueobject.StoreAddress;

public class BookOutOfPrint
{
    private final StoreAddress storeAddress;
    private final ISBN13 isbn13;
    private final int availableCopies;

    public BookOutOfPrint(StoreAddress storeAddress, ISBN13 isbn13, int availableCopies)
    {
        this.storeAddress = storeAddress;
        this.isbn13 = isbn13;
        this.availableCopies = availableCopies;
    }

    public int avaliableCopies()
    {
        return availableCopies;
    }

    public StoreAddress getStoreAddress()
    {
        return storeAddress;
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
                Objects.equals(storeAddress, that.storeAddress) &&
                Objects.equals(isbn13, that.isbn13);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(storeAddress, isbn13, availableCopies);
    }
}
