package io.jexxa.adapterapi.invocation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultInvocationHandlerTest {

    private CountingObject objectUnderTest;
    private InvocationHandler invocationHandler;
    private Method invocationMethod;


    @BeforeEach
    void initTest() throws NoSuchMethodException
    {
        objectUnderTest = new CountingObject();
        invocationHandler = getInvocationHandler(objectUnderTest);
        invocationMethod = objectUnderTest.getClass().getMethod("increment");
    }


    @Test
    void invokeTest() throws InvocationTargetException, IllegalAccessException
    {
        //Act
        invocationHandler.invoke(invocationMethod , objectUnderTest, new Object[0]);

        //Assert
        assertEquals(1, objectUnderTest.getCounter());
    }

    @Test
    void invokeWithDoubleInterceptorBefore() throws InvocationTargetException, IllegalAccessException
    {
        //Arrange
        AtomicInteger interceptingBeforeResult = new AtomicInteger(0);

        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .registerBefore(invocationContext -> { interceptingBeforeResult.set(objectUnderTest.getCounter()); invocationContext.invoke();});

        //Act
        invocationHandler.invoke(invocationMethod, objectUnderTest, new Object[0]);

        //Assert
        assertEquals(0, interceptingBeforeResult.get());
        assertEquals(2, objectUnderTest.getCounter());
    }


    @Test
    void invokeWithDoubleInterceptorAfter() throws InvocationTargetException, IllegalAccessException
    {
        //Arrange
        AtomicInteger interceptingAfterResult = new AtomicInteger(0);
        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .registerAfter(invocationContext -> { interceptingAfterResult.set(objectUnderTest.getCounter()); invocationContext.invoke();});

        //Act
        invocationHandler.invoke(invocationMethod, objectUnderTest, new Object[0]);

        //Assert
        assertEquals(1, interceptingAfterResult.get());
        assertEquals(2, objectUnderTest.getCounter());
    }

    @Test
    void invokeWithDoubleInterceptorAround() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        //Arrange
        AtomicInteger interceptingAroundBeforeResult = new AtomicInteger(0);
        AtomicInteger interceptingAroundAfterResult = new AtomicInteger(0);

        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .registerAround(invocationContext -> {
                    interceptingAroundBeforeResult.set(objectUnderTest.getCounter());
                    invocationContext.proceed();
                    interceptingAroundAfterResult.set(objectUnderTest.getCounter());}
                );

        //Act
        invocationHandler.invoke(objectUnderTest.getClass().getMethod("increment"), objectUnderTest, new Object[0]);

        //Assert
        assertEquals(0, interceptingAroundBeforeResult.get());
        assertEquals(1, interceptingAroundAfterResult.get());
        assertEquals(1, objectUnderTest.getCounter());
    }

    @Test
    void invokeWithAllDoubleInterceptors() throws InvocationTargetException, IllegalAccessException
    {
        final Integer[] interceptingResults = new Integer[3];

        //Arrange
        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .registerBefore(invocationContext -> { invocationContext.invoke(); interceptingResults[0]  = objectUnderTest.getCounter(); })
                .registerAround(invocationContext -> { invocationContext.invoke(); interceptingResults[1]  = objectUnderTest.getCounter(); invocationContext.proceed();})
                .registerAfter(invocationContext -> {  interceptingResults[2]  = objectUnderTest.getCounter(); invocationContext.invoke();});

        //Act
        invocationHandler.invoke(invocationMethod , objectUnderTest, new Object[0]);

        //Assert
        assertEquals(1, interceptingResults[0]);
        assertEquals(2, interceptingResults[1]);
        assertEquals(3, interceptingResults[2]);
        assertEquals(4, objectUnderTest.getCounter());
    }

}