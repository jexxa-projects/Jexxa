package io.jexxa.tutorials.bookstore.domain.aggregate;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jexxa.tutorials.bookstore.domain.businessexception.InvalidISBNException;
import io.jexxa.tutorials.bookstore.domain.valueobject.BookStore;
import io.jexxa.tutorials.bookstore.domain.valueobject.ISBN13;
import io.jexxa.tutorials.bookstore.domainservice.ISBNService;
import org.junit.jupiter.api.Test;

class BookStockTest
{
    @Test
    void receiveBook() throws InvalidISBNException
    {
        //Arrange
        ISBN13 isbn13Number = ISBNService.convertFrom("978-3-86490-387-8");
        int amount = 5;
        var bookStore = new BookStore(12345, "MyBookStoreStreet", 5);
        var objectUnderTest = BookStock.create(bookStore);

        //Act
        objectUnderTest.receiveBook(isbn13Number, amount);

        //Assert
        assertTrue(objectUnderTest.inStock(isbn13Number));
        assertEquals(amount, objectUnderTest.amountInStock(isbn13Number));
    }

}