package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import io.ddd.jexxa.application.applicationservice.SimpleApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag("unit-test")
public class RESTfulRPCModelTest
{

    @Test
    public void validateGETCommands()
    {
        var defaultObject = new SimpleApplicationService();
        defaultObject.setSimpleValue(42);
        var objectUnderTest = new RESTfulRPCModel(defaultObject);

        var result = objectUnderTest.getGETCommands();

        // Check all conventions as defined in {@link RESTfulRPCModel}.
        Assertions.assertFalse(result.isEmpty());

        //Check that all commands are marked as GET
        result.forEach(element -> Assertions.assertEquals(RESTfulRPCModel.RESTfulRPCMethod.HTTPCommand.GET,
                element.getHTTPCommand()));

        //Check URIs
        result.forEach(element -> Assertions.assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //Check return types are NOT void
        result.forEach(element -> Assertions.assertNotEquals(void.class, element.getMethod().getReturnType()));

    }

    @Test
    public void validatePOSTCommands()
    {
        var defaultObject = new SimpleApplicationService();
        defaultObject.setSimpleValue(42);
        var objectUnderTest = new RESTfulRPCModel(defaultObject);

        var result = objectUnderTest.getPOSTCommands();

        //Check all conventions as defined in {@link RESTfulRPCGenerator}.
        Assertions.assertFalse(result.isEmpty());

        //Check that all commands are marked as GET
        result.forEach(element -> Assertions.assertEquals(RESTfulRPCModel.RESTfulRPCMethod.HTTPCommand.POST,
                element.getHTTPCommand()));

        //Check URIs
        result.forEach(element -> Assertions.assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //Check return types are NOT void or Parameter > 0 
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
