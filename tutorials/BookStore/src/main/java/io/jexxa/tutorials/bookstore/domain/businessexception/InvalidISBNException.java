package io.jexxa.tutorials.bookstore.domain.businessexception;

public class InvalidISBNException extends Exception
{
    public InvalidISBNException()
    {
        //Public constructor 
    }

    public InvalidISBNException(Exception e)
    {
        super(e);
    }
}
