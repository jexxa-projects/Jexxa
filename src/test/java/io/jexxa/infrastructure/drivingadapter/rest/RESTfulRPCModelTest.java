package io.jexxa.infrastructure.drivingadapter.rest;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag("unit-test")
public class RESTfulRPCModelTest
{
    private RESTfulRPCModel objectUnderTest;


    @BeforeEach
    public void setupTests()
    {
        SimpleApplicationService simpleApplicationService = new SimpleApplicationService();
        simpleApplicationService.setSimpleValue(42);
        objectUnderTest = new RESTfulRPCModel(simpleApplicationService);
    }

    @Test
    public void validateGETCommands()
    {
        //Act
        var result = objectUnderTest.getGETCommands();

        //Assert
        //1. Check all conventions as defined in {@link RESTfulRPCModel}.
        Assertions.assertFalse(result.isEmpty());

        //2. Check that all commands are marked as GET
        result.forEach(element -> Assertions.assertEquals(RESTfulRPCModel.RESTfulRPCMethod.HTTPCommand.GET,
                element.getHTTPCommand()));

        //3. Check URIs
        result.forEach(element -> Assertions.assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //4. Check return types are NOT void
        result.forEach(element -> Assertions.assertNotEquals(void.class, element.getMethod().getReturnType()));
    }

    @Test
    public void validatePOSTCommands()
    {
        //Act
        var result = objectUnderTest.getPOSTCommands();

        //Assert 
        //1.Check all conventions as defined in {@link RESTfulRPCGenerator}.
        Assertions.assertFalse(result.isEmpty());

        //2.Check that all commands are marked as GET
        result.forEach(element -> Assertions.assertEquals(RESTfulRPCModel.RESTfulRPCMethod.HTTPCommand.POST,
                element.getHTTPCommand()));

        //3.Check URIs
        result.forEach(element -> Assertions.assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //4.Check return types are NOT void or Parameter > 0
        result.forEach(element -> Assertions.assertTrue( (void.class.equals(element.getMethod().getReturnType())
                                            || element.getMethod().getParameterCount() > 0 )));

    }

    @Test
    public void invalidApplicationService()
    {
        //Act / Assert
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new RESTfulRPCModel(new UnsupportedApplicationService())
        );
    }

}
