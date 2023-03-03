package io.jexxa.drivenadapter.strategy.messaging;

import io.jexxa.drivenadapter.strategy.MessageSenderManager;
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