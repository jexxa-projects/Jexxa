package io.jexxa.adapterapi.invocation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InvocationHandlerTest {
    private CountingObject objectUnderTest;
    private InvocationHandler invocationHandler;

    @BeforeEach
    void setUp()
    {
        objectUnderTest = new CountingObject();
        invocationHandler = getInvocationHandler(objectUnderTest);
    }

    @Test
    void invokeRunnable()
    {
        //Act
        invocationHandler.invoke(objectUnderTest, objectUnderTest::increment);

        //Assert
        assertEquals(1, objectUnderTest.getCounter());
    }

    @Test
    void invokeConsumer()
    {
        //Arrange
        int element = 100;

        //Act
        invocationHandler.invoke(  objectUnderTest::setCounter, element );

        //Assert
        assertEquals(element, objectUnderTest.getCounter());
    }

    @Test
    void invokeSupplier()
    {
        //Arrange
        int element = 100;
        invocationHandler.invoke(  objectUnderTest::setCounter, element );

        //Act
        var result = invocationHandler.invoke(  objectUnderTest::getCounter );

        //Assert
        assertEquals(element, result);
    }

    @Test
    void invokeFunction()
    {
        //Arrange
        int element = 100;

        //Act
        var result = invocationHandler.invoke(  objectUnderTest::setGetCounter, element );

        //Assert
        assertEquals(element, result);
    }
}