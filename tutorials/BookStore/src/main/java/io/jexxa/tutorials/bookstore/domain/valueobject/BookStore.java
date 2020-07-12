package io.jexxa.tutorials.bookstore.domain.valueobject;

import java.util.Objects;

public class BookStore
{
    private final int zipCode;
    private final String street;
    private final int houseNumber;

    public BookStore(int zipCode, String street, int houseNumber )
    {
        this.zipCode = zipCode;
        this.street = street;
        this.houseNumber = houseNumber;
    }

    public int getHouseNumber()
    {
        return houseNumber;
    }

    public int getZipCode()
    {
        return zipCode;
    }

    public String getStreet()
    {
        return street;
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
        BookStore that = (BookStore) o;
        return zipCode == that.zipCode &&
                houseNumber == that.houseNumber &&
                Objects.equals(street, that.street);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(zipCode, street, houseNumber);
    }
}
