package io.jexxa.tutorials.bookstorej16.domain.businessexception;

/**
 * Is thrown in case we try to sell a book that is currently not in stock
 */
public class BookNotInStockException extends Exception
{
    private static final long serialVersionUID = 1L;
}
