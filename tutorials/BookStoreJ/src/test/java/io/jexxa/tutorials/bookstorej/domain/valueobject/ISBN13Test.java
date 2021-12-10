package io.jexxa.tutorials.bookstorej.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ISBN13Test
{

    @Test
    void testEquals()
    {
        //Arrange
        var referenceISBN13 = new ISBN13("978-1-60309-025-4");
        var objectUnderTest = new ISBN13("978-1-60309-025-4");

        //Act/assert
        assertEquals(referenceISBN13, objectUnderTest);
    }

}