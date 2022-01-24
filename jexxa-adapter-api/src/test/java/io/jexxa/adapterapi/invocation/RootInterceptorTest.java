package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.interceptor.DefaultInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RootInterceptorTest {

    private TestObject objectUnderTest;
    private InvocationHandler invocationHandler;
    private Method invocationMethod;


    @BeforeEach
    void initTest() throws NoSuchMethodException
    {
        objectUnderTest = new TestObject();
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
        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .register(new DoubleInterceptorBefore());

        //Act
        invocationHandler.invoke(invocationMethod, objectUnderTest, new Object[0]);

        //Assert
        assertEquals(2, objectUnderTest.getCounter());
    }


    @Test
    void invokeWithDoubleInterceptorAfter() throws InvocationTargetException, IllegalAccessException
    {
        //Arrange
        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .register(new DoubleInterceptorAfter());

        //Act
        invocationHandler.invoke(invocationMethod, objectUnderTest, new Object[0]);

        //Assert
        assertEquals(2, objectUnderTest.getCounter());
    }

    @Test
    void invokeWithDoubleInterceptorAround() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
    {
        //Arrange
        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .register(new DoubleInterceptorAround());

        //Act
        invocationHandler.invoke(objectUnderTest.getClass().getMethod("increment"), objectUnderTest, new Object[0]);

        //Assert
        assertEquals(2, objectUnderTest.getCounter());
    }

    @Test
    void invokeWithAllDoubleInterceptors() throws InvocationTargetException, IllegalAccessException
    {
        //Arrange
        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .register(new DoubleInterceptorBefore())
                .register(new DoubleInterceptorAround())
                .register(new DoubleInterceptorAfter());

        //Act
        invocationHandler.invoke(invocationMethod , objectUnderTest, new Object[0]);

        //Assert
        assertEquals(4, objectUnderTest.getCounter());
    }

    public static class DoubleInterceptorBefore extends DefaultInterceptor
    {
        @Override
        public void before(InvocationContext invocationContext)
        {
            try {
                invocationContext.getMethod().invoke(invocationContext.getTarget(), invocationContext.getArgs());
            } catch (InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }


    public static class DoubleInterceptorAfter extends DefaultInterceptor
    {
        @Override
        public void after(InvocationContext invocationContext)
        {
            try {
                invocationContext.getMethod().invoke(invocationContext.getTarget(), invocationContext.getArgs());
            } catch (InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }


    public static class DoubleInterceptorAround extends DefaultInterceptor
    {
        @Override
        public void around(InvocationContext invocationContext) throws InvocationTargetException, IllegalAccessException {
            try {
                invocationContext.getMethod().invoke(invocationContext.getTarget(), invocationContext.getArgs());
            } catch (InvocationTargetException | IllegalAccessException e)
            {
                e.printStackTrace();
            }

            invocationContext.proceed();
        }
    }

}