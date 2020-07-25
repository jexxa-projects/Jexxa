package io.jexxa.infrastructure.drivenadapterstrategy.messaging;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MessageSenderManagerTest
{


    @Test
    void setInvalidDefaultStrategy()
    {
        //Arrange 
        var objectUnderTest = MessageSenderManager.getInstance();

        //Act/Assert
        assertThrows( NullPointerException.class, () -> objectUnderTest.setDefaultStrategy(null));
    }

}