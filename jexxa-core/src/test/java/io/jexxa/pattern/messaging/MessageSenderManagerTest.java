package io.jexxa.pattern.messaging;

import io.jexxa.pattern.MessageSenderManager;
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