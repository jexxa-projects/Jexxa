package io.jexxa.tutorials.bookstore.domain.domainevent;

import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public record BookSoldOut(ISBN13 isbn13)
{
    public static BookSoldOut bookSoldOut(ISBN13 isbn13)
    {
        return new BookSoldOut(isbn13);
    }
}
