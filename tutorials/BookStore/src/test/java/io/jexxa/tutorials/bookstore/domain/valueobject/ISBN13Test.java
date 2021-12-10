package io.jexxa.tutorials.bookstore.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ISBN13Test
{
    @Test
    void testValidISBN13()
    {
        //Arrange
        var isbn13 = "978-3-86490-387-8";

        //Act/Assert
        assertDoesNotThrow(() -> new ISBN13(isbn13));
    }

    @Test
    void testInvalidISBN13()
    {
        //Arrange
        var isbn13 = "978-3-86490-387-0"; //Invalid checksum

        //Act/Assert
        assertThrows(IllegalArgumentException.class, () -> new ISBN13(isbn13));
    }

}