package io.jexxa.core;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.application.applicationservice.IncrementApplicationService;
import io.jexxa.utils.JexxaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JexxaMainInterceptorTest
{
    private JexxaMain objectUnderTest;

    @BeforeEach
    void initTest()
    {
        objectUnderTest = new JexxaMain( JexxaMainInterceptorTest.class );
    }

    @Test
    void testLoggingInterceptor() {
        //Arrange
        objectUnderTest
                .intercept( IncrementApplicationService.class )
                .before( invocationContext -> JexxaLogger.getLogger(IncrementApplicationService.class)
                        .info( "Call method " + invocationContext.getMethod().getName( ))
                );

        var targetObject = objectUnderTest.getInstanceOfPort(IncrementApplicationService.class);
        var invocationHandler = InvocationManager.getInvocationHandler(targetObject);

        //Act - DrivingAdapter View
        invocationHandler.invoke(targetObject, targetObject::increment);
        //invocationHandler.invoke(IncrementApplicationService.class.getMethod("increment"), targetObject, new Object[0]);
    }

}
