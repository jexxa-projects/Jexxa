package io.jexxa.tutorials.bookstore.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class ISBN13Test
{
    @Test
    void testValidISBN13()
    {
        //Arrange
        var registrationGroup = new RegistrationGroup(3);
        var registrant = new Registrant(86490);
        var publication = new Publication(387);
        var validDigit = new CheckDigit(8);
        
        assertDoesNotThrow(() -> new ISBN13(Prefix.PREFIX_978, registrationGroup, registrant, publication, validDigit));
    }

    @Test
    void testInvalidISBN13()
    {
        //Arrange
        var registrationGroup = new RegistrationGroup(3);
        var registrant = new Registrant(86490);
        var publication = new Publication(387);
        var invalidDigit = new CheckDigit(0);

        assertThrows(IllegalArgumentException.class, () -> new ISBN13(Prefix.PREFIX_978, registrationGroup, registrant, publication, invalidDigit));
    }
}