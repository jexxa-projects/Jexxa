package io.jexxa.tutorials.bookstorej.domain.domainevent;

import io.jexxa.addend.applicationcore.DomainEvent;
import io.jexxa.tutorials.bookstorej.domain.valueobject.ISBN13;

@DomainEvent("BookSoldOut")
public final class BookSoldOut
{
    private final ISBN13 isbn13;

    private BookSoldOut(ISBN13 isbn13)
    {
        this.isbn13 = isbn13;
    }

    public ISBN13 getISBN13()
    {
        return isbn13;
    }

    public static BookSoldOut bookSoldOut(ISBN13 isbn13)
    {
        return new BookSoldOut(isbn13);
    }
}
