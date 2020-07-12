package io.jexxa.tutorials.bookstore.domainservice;


import io.jexxa.tutorials.bookstore.domain.businessexception.InvalidISBNException;
import io.jexxa.tutorials.bookstore.domain.valueobject.CheckDigit;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstore.domain.valueobject.Prefix;
import io.jexxa.tutorials.bookstore.domain.valueobject.Publication;
import io.jexxa.tutorials.bookstore.domain.valueobject.Registrant;
import io.jexxa.tutorials.bookstore.domain.valueobject.RegistrationGroup;

public class ISBNService
{
    public static ISBN13 convertFrom(String isbnAsString) throws InvalidISBNException
    {
        var result = isbnAsString.split("-");
        if (result.length != 5) {
            throw new InvalidISBNException();
        }

        try
        {
            return new ISBN13(
                    new Prefix(Integer.parseInt(result[0])),
                    new RegistrationGroup(Integer.parseInt(result[1])),
                    new Registrant(Integer.parseInt(result[2])),
                    new Publication(Integer.parseInt(result[3])),
                    new CheckDigit(Integer.parseInt(result[4]))

            );
        } catch (IllegalArgumentException e)
        {
            throw new InvalidISBNException();
        }
    }
}
