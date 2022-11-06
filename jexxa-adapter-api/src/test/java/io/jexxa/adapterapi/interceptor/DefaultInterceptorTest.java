package io.jexxa.adapterapi.interceptor;

import io.jexxa.adapterapi.CountingObject;
import org.junit.jupiter.api.Test;

import static io.jexxa.adapterapi.invocation.InvocationManager.getInvocationHandler;
import static io.jexxa.adapterapi.invocation.InvocationManager.getRootInterceptor;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultInterceptorTest {
    @Test
    void testForwardMethodCall() {
        //Arrange
        var countingObject = new CountingObject();
        var invocationHandler = getInvocationHandler(countingObject);
        var objectUnderTest = new DefaultInterceptor();

        getRootInterceptor(countingObject).registerBefore(objectUnderTest);
        getRootInterceptor(countingObject).registerAfter(objectUnderTest);
        getRootInterceptor(countingObject).registerAround(objectUnderTest);

        //Act
        invocationHandler.invoke(countingObject, countingObject::increment);

        //Assert
        assertEquals(1, countingObject.getCounter());
    }
}