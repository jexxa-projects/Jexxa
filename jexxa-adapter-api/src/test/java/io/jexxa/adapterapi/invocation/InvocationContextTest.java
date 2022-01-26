package io.jexxa.adapterapi.invocation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.adapterapi.invocation.InvocationManager.getRootInterceptor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class InvocationContextTest {
    private CountingObject objectUnderTest;
    private InvocationHandler invocationHandler;
    private Method invocationMethod;
    private final InvocationContext[] resultingContext = new InvocationContext[4];

    private static final int incrementContext = 0;
    private static final int getCounterContext = 1;
    private static final int setCounterContext = 2;
    private static final int setGetCounterContext = 3;



    @BeforeEach
    void initTest() throws NoSuchMethodException
    {
        objectUnderTest = new CountingObject();
        invocationHandler = getInvocationHandler(objectUnderTest);
        invocationMethod = objectUnderTest.getClass().getMethod("increment");

        getRootInterceptor(objectUnderTest).registerBefore(invocationContext -> {
            if (invocationContext.getMethod().getName().equals("increment")) {
                resultingContext[incrementContext] = invocationContext;
            }
            if (invocationContext.getMethod().getName().equals("setCounter")) {
                resultingContext[setCounterContext] = invocationContext;
            }
            if (invocationContext.getMethod().getName().equals("getCounter")) {
                resultingContext[getCounterContext] = invocationContext;
            }
            if (invocationContext.getMethod().getName().equals("setGetCounter")) {
                resultingContext[setGetCounterContext] = invocationContext;
            }
        });
    }

    @Test
    void testGetMethod() throws InvocationTargetException, IllegalAccessException {
        //Act
        invokeTestMethods();

        //Assert
        assertEquals("increment", resultingContext[incrementContext].getMethod().getName());
        assertEquals("setCounter", resultingContext[setCounterContext].getMethod().getName());
        assertEquals("getCounter", resultingContext[getCounterContext].getMethod().getName());
        assertEquals("setGetCounter", resultingContext[setGetCounterContext].getMethod().getName());
    }

    @Test
    void testGetTarget() throws InvocationTargetException, IllegalAccessException {
        //Act
        invokeTestMethods();

        //Assert
        assertEquals(objectUnderTest, resultingContext[incrementContext].getTarget());
        assertEquals(objectUnderTest, resultingContext[incrementContext].getTarget(CountingObject.class));

        assertEquals(objectUnderTest, resultingContext[setCounterContext].getTarget());
        assertEquals(objectUnderTest, resultingContext[setCounterContext].getTarget(CountingObject.class));

        assertEquals(objectUnderTest, resultingContext[getCounterContext].getTarget());
        assertEquals(objectUnderTest, resultingContext[getCounterContext].getTarget(CountingObject.class));

        assertEquals(objectUnderTest, resultingContext[setGetCounterContext].getTarget());
        assertEquals(objectUnderTest, resultingContext[setGetCounterContext].getTarget(CountingObject.class));
    }

    @Test
    void getReturnValue() throws InvocationTargetException, IllegalAccessException {
        //Act
        invokeTestMethods();

        //Assert
        assertNull(resultingContext[incrementContext].getReturnValue(Integer.class));
        assertEquals(2, resultingContext[getCounterContext].getReturnValue(Integer.class));
        assertNull(resultingContext[setCounterContext].getReturnValue());
        assertEquals(100, resultingContext[setGetCounterContext].getReturnValue(Integer.class));
    }

    private void invokeTestMethods() throws InvocationTargetException, IllegalAccessException {
        invocationHandler.invoke(invocationMethod, objectUnderTest, new Object[0]);
        invocationHandler.invoke(objectUnderTest, objectUnderTest::increment);
        invocationHandler.invoke(objectUnderTest, objectUnderTest::getCounter);
        invocationHandler.invoke(objectUnderTest, objectUnderTest::setCounter, 100);
        invocationHandler.invoke(objectUnderTest, objectUnderTest::setGetCounter, 100);
    }
}