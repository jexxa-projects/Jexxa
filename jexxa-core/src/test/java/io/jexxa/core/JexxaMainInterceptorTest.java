package io.jexxa.core;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.application.applicationservice.IncrementApplicationService;
import io.jexxa.utils.JexxaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JexxaMainInterceptorTest
{
    private JexxaMain objectUnderTest;

    @BeforeEach
    void initTest()
    {
        objectUnderTest = new JexxaMain( JexxaMainInterceptorTest.class );
    }

    @Test
    void testLoggingInterceptor()
    {
        //Arrange
        var targetObject = objectUnderTest.getInstanceOfPort(IncrementApplicationService.class);
        var invocationHandler = InvocationManager.getInvocationHandler(targetObject);
        var result = new String[3];

        objectUnderTest
                .intercept( targetObject )
                .beforeAnd( invocationContext -> result[0] = "Before " + invocationContext.getMethod().getName( ))
                .aroundAnd( invocationContext -> result[1] = "Around " + invocationContext.getMethod().getName( ))
                .after( invocationContext -> result[2] = "After " +  invocationContext.getMethod().getName( ));


        //Act - DrivingAdapter View
        invocationHandler.invoke(targetObject, targetObject::increment);

        //Assert
        assertEquals("Before increment", result[0]);
        assertEquals("Around increment", result[1]);
        assertEquals("After increment", result[2]);
    }

}
