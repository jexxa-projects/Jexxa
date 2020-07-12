package io.jexxa.tutorials.bookstore.applicationservice;

import java.util.ArrayList;
import java.util.List;

import io.jexxa.tutorials.bookstore.domain.businessexception.BookNotInStockException;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;

public class BookStoreService
{
    public void sellBook(ISBN13 isbn13) throws BookNotInStockException
    {
        
    }

    public void addNewBook(ISBN13 isbn13)
    {
        
    }
    
    public List<ISBN13> getAllAvailableBooks()
    {
        return new ArrayList<>();
    }

    public void newDelivery(ISBN13 isbn13, int amount)
    {
        
    }

}
