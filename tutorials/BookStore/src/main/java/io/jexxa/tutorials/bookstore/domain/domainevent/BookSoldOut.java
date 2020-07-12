package io.jexxa.tutorials.bookstore.domain.domainevent;

import java.util.Objects;

import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstore.domain.valueobject.StoreAddress;

public class BookSoldOut
{
    private final StoreAddress storeAddress;
    private final ISBN13 isbn13;

    public BookSoldOut(StoreAddress storeAddress, ISBN13 isbn13)
    {
        this.storeAddress = storeAddress;
        this.isbn13 = isbn13;
    }

    public ISBN13 getISBN13()
    {
        return isbn13;
    }

    public StoreAddress getStoreAddress()
    {
        return storeAddress;
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
        return Objects.equals(storeAddress, that.storeAddress) &&
                Objects.equals(isbn13, that.isbn13);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(storeAddress, isbn13);
    }
}
