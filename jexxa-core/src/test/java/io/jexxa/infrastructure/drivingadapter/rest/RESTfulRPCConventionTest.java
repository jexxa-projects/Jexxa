package io.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class RESTfulRPCConventionTest
{
    private RESTfulRPCConvention objectUnderTest;


    @BeforeEach
    void setupTests()
    {
        SimpleApplicationService simpleApplicationService = new SimpleApplicationService();
        simpleApplicationService.setSimpleValue(42);
        objectUnderTest = new RESTfulRPCConvention(simpleApplicationService);
    }

    @Test
    void validateGETCommands()
    {
        //Act
        var result = objectUnderTest.getGETCommands();

        //Assert
        //1. Check all conventions as defined in {@link RESTfulRPCModel}.
        assertFalse(result.isEmpty());

        //2. Check that all commands are marked as GET
        result.forEach(element -> assertEquals(RESTfulRPCConvention.RESTfulRPCMethod.HTTPCommand.GET,
                element.getHTTPCommand()));

        //3. Check URIs
        result.forEach(element -> assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //4. Check return types are NOT void
        result.forEach(element -> assertNotEquals(void.class, element.getMethod().getReturnType()));
    }

    @Test
    void validatePOSTCommands()
    {
        //Act
        var result = objectUnderTest.getPOSTCommands();

        //Assert 
        //1.Check all conventions as defined in {@link RESTfulRPCGenerator}.
        assertFalse(result.isEmpty());

        //2.Check that all commands are marked as GET
        result.forEach(element -> assertEquals(RESTfulRPCConvention.RESTfulRPCMethod.HTTPCommand.POST,
                element.getHTTPCommand()));

        //3.Check URIs
        result.forEach(element -> assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //4.Check return types are NOT void or Parameter > 0
        result.forEach(element -> assertTrue( (void.class.equals(element.getMethod().getReturnType())
                                            || element.getMethod().getParameterCount() > 0 )));

    }

    @Test
    void invalidApplicationService()
    {
        //Arrange
        var objectUnderTest = new UnsupportedApplicationService();

        //Act / Assert
        assertThrows(IllegalArgumentException.class, () ->
                new RESTfulRPCConvention(objectUnderTest)
        );
    }

}
