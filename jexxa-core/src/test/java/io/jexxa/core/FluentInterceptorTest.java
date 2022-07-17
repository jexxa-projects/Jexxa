package io.jexxa.core;

import io.jexxa.adapterapi.invocation.InvocationManager;
import io.jexxa.application.annotation.ValidApplicationService;
import io.jexxa.application.applicationservice.IncrementApplicationService;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.RepositoryManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.imdb.IMDBRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.TestConstants.JEXXA_DRIVEN_ADAPTER;
import static io.jexxa.TestConstants.JEXXA_DRIVING_ADAPTER;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FluentInterceptorTest
{
    private JexxaMain objectUnderTest;

    @BeforeEach
    void initTest()
    {
        objectUnderTest = new JexxaMain( FluentInterceptorTest.class );
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
        objectUnderTest = new JexxaMain(FluentInterceptorTest.class);
        objectUnderTest
                .addToInfrastructure(JEXXA_DRIVEN_ADAPTER)
                .addToInfrastructure(JEXXA_DRIVING_ADAPTER)
                .addToApplicationCore(JEXXA_APPLICATION_SERVICE);

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
