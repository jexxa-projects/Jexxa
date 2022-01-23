package io.jexxa.adapterapi.invocation;

import io.jexxa.adapterapi.interceptor.Interceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RootInterceptorTest {

    private TestObject objectUnderTest;
    private InvocationHandler invocationHandler;
    InvocationContext incrementInvocationContext;


    @BeforeEach
    void initTest() throws NoSuchMethodException
    {
        objectUnderTest = new TestObject();
        invocationHandler = getInvocationHandler(objectUnderTest);
        incrementInvocationContext = new InvocationContext( objectUnderTest.getClass().getMethod("increment"), objectUnderTest, new Object[0] );
    }


    @Test
    void invokeTest() throws InvocationTargetException, IllegalAccessException {
        //Act
        invocationHandler.invoke(incrementInvocationContext);

        //Assert
        assertEquals(1, objectUnderTest.getCounter());
    }

    @Test
    void invokeWithDoubleInterceptor() throws InvocationTargetException, IllegalAccessException {
        //Arrange
        InvocationManager
                .getRootInterceptor(objectUnderTest)
                .register(new DoubleInterceptor());

        //Act
        invocationHandler.invoke(incrementInvocationContext);

        //Assert
        assertEquals(2, objectUnderTest.getCounter());

    }


    public static class DoubleInterceptor implements Interceptor
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


}