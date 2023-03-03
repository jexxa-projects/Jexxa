package io.jexxa.core;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.application.JexxaTestApplication;
import io.jexxa.application.annotation.ValidApplicationService;
import io.jexxa.application.applicationservice.IncrementApplicationService;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.drivenadapter.strategy.RepositoryManager;
import io.jexxa.drivenadapter.strategy.persistence.repository.imdb.IMDBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FluentInterceptorTest
{
    private JexxaMain objectUnderTest;

    @BeforeEach
    void initTest()
    {
        objectUnderTest = new JexxaMain( JexxaTestApplication.class );
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
                .aroundAnd( invocationContext -> {
                    result[1] = "Around " + invocationContext.getMethod().getName();
                    invocationContext.proceed();}
                )
                .after( invocationContext -> result[2] = "After " +  invocationContext.getMethod().getName( ));


        //Act - DrivingAdapter View
        invocationHandler.invoke(targetObject, targetObject::increment);

        //Assert
        assertEquals("Before increment", result[0]);
        assertEquals("Around increment", result[1]);
        assertEquals("After increment", result[2]);

        assertEquals(1, targetObject.getCounter());
    }


    @Test
    void testLoggingInterceptorWithClass()
    {
        //Arrange
        var targetObject = objectUnderTest.getInstanceOfPort(IncrementApplicationService.class);
        var invocationHandler = InvocationManager.getInvocationHandler(targetObject);
        var result = new String[3];

        objectUnderTest
                .intercept( IncrementApplicationService.class )
                .beforeAnd( invocationContext -> result[0] = "Before " + invocationContext.getMethod().getName( ))
                .aroundAnd( invocationContext -> {
                    result[1] = "Around " + invocationContext.getMethod().getName();
                    invocationContext.proceed();}
                )
                .after( invocationContext -> result[2] = "After " +  invocationContext.getMethod().getName( ));


        //Act - DrivingAdapter View
        invocationHandler.invoke(targetObject, targetObject::increment);

        //Assert
        assertEquals("Before increment", result[0]);
        assertEquals("Around increment", result[1]);
        assertEquals("After increment", result[2]);

        assertEquals(1, targetObject.getCounter());
    }

    @Test
    void testLoggingInterceptorWithAnnotation()
    {
        //Arrange
        RepositoryManager.setDefaultStrategy(IMDBRepository.class);
        objectUnderTest = new JexxaMain(JexxaTestApplication.class);

        var targetObject = objectUnderTest.getInstanceOfPort(SimpleApplicationService.class);
        var invocationHandler = InvocationManager.getInvocationHandler(targetObject);
        var result = new String[3];

        objectUnderTest
                .interceptAnnotation( ValidApplicationService.class )
                .beforeAnd( invocationContext -> result[0] = "Before " + invocationContext.getMethod().getName( ))
                .aroundAnd( invocationContext -> {
                    result[1] = "Around " + invocationContext.getMethod().getName();
                    invocationContext.proceed(); }
                )
                .after( invocationContext -> result[2] = "After " +  invocationContext.getMethod().getName( ));


        //Act - DrivingAdapter View
        invocationHandler.invoke(targetObject, targetObject::getMessages);

        //Assert
        assertEquals("Before getMessages", result[0]);
        assertEquals("Around getMessages", result[1]);
        assertEquals("After getMessages", result[2]);
    }

}
