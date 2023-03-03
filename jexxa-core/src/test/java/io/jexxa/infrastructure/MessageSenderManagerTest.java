package io.jexxa.infrastructure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MessageSenderManagerTest
{
    @Test
    void setInvalidDefaultStrategy()
    {
        //Arrange - nothing

        //Act/Assert
        assertThrows( NullPointerException.class, () -> MessageSenderManager.setDefaultStrategy(null));
    }

}