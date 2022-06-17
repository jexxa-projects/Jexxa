package io.jexxa.adapterapi.invocation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    private static final int INCREMENT_CONTEXT = 0;
    private static final int GET_COUNTER_CONTEXT = 1;
    private static final int SET_COUNTER_CONTEXT = 2;
    private static final int SET_GET_COUNTER_CONTEXT = 3;



    @BeforeEach
    void initTest() throws NoSuchMethodException
    {
        objectUnderTest = new CountingObject();
        invocationHandler = getInvocationHandler(objectUnderTest);
        invocationMethod = objectUnderTest.getClass().getMethod("increment");

        getRootInterceptor(objectUnderTest).registerBefore(invocationContext -> {
            if (invocationContext.getMethod().getName().equals("increment")) {
                resultingContext[INCREMENT_CONTEXT] = invocationContext;
            }
            if (invocationContext.getMethod().getName().equals("setCounter")) {
                resultingContext[SET_COUNTER_CONTEXT] = invocationContext;
            }
            if (invocationContext.getMethod().getName().equals("getCounter")) {
                resultingContext[GET_COUNTER_CONTEXT] = invocationContext;
            }
            if (invocationContext.getMethod().getName().equals("setGetCounter")) {
                resultingContext[SET_GET_COUNTER_CONTEXT] = invocationContext;
            }
        });
    }

    @Test
    void testGetMethod() {
        //Act
        invokeTestMethods();

        //Assert
        assertEquals("increment", resultingContext[INCREMENT_CONTEXT].getMethod().getName());
        assertEquals("setCounter", resultingContext[SET_COUNTER_CONTEXT].getMethod().getName());
        assertEquals("getCounter", resultingContext[GET_COUNTER_CONTEXT].getMethod().getName());
        assertEquals("setGetCounter", resultingContext[SET_GET_COUNTER_CONTEXT].getMethod().getName());
    }

    @Test
    void testGetTarget() {
        //Act
        invokeTestMethods();

        //Assert
        assertEquals(objectUnderTest, resultingContext[INCREMENT_CONTEXT].getTarget());
        assertEquals(objectUnderTest, resultingContext[INCREMENT_CONTEXT].getTarget(CountingObject.class));

        assertEquals(objectUnderTest, resultingContext[SET_COUNTER_CONTEXT].getTarget());
        assertEquals(objectUnderTest, resultingContext[SET_COUNTER_CONTEXT].getTarget(CountingObject.class));

        assertEquals(objectUnderTest, resultingContext[GET_COUNTER_CONTEXT].getTarget());
        assertEquals(objectUnderTest, resultingContext[GET_COUNTER_CONTEXT].getTarget(CountingObject.class));

        assertEquals(objectUnderTest, resultingContext[SET_GET_COUNTER_CONTEXT].getTarget());
        assertEquals(objectUnderTest, resultingContext[SET_GET_COUNTER_CONTEXT].getTarget(CountingObject.class));
    }

    @Test
    void getReturnValue() {
        //Act
        invokeTestMethods();

        //Assert
        assertNull(resultingContext[INCREMENT_CONTEXT].getReturnValue(Integer.class));
        assertEquals(2, resultingContext[GET_COUNTER_CONTEXT].getReturnValue(Integer.class));
        assertNull(resultingContext[SET_COUNTER_CONTEXT].getReturnValue());
        assertEquals(100, resultingContext[SET_GET_COUNTER_CONTEXT].getReturnValue(Integer.class));
    }

    private void invokeTestMethods() {
        invocationHandler.invoke(invocationMethod, objectUnderTest, new Object[0]);
        invocationHandler.invoke(objectUnderTest, objectUnderTest::increment);
        invocationHandler.invoke(objectUnderTest, objectUnderTest::getCounter);
        invocationHandler.invoke(objectUnderTest, objectUnderTest::setCounter, 100);
        invocationHandler.invoke(objectUnderTest, objectUnderTest::setGetCounter, 100);
    }
}