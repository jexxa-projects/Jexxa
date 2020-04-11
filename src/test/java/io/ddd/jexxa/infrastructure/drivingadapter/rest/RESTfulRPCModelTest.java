package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import io.ddd.jexxa.application.applicationservice.SimpleApplicationService;
import org.junit.Test;

public class RESTfulRPCModelTest
{

    @Test
    public void validateGETCommands()
    {
        var defaultObject = new SimpleApplicationService(42);
        var objectUnderTest = new RESTfulRPCModel(defaultObject);

        var result = objectUnderTest.getGETCommands();
        
        //Check all conventions as defined in {@link RESTfulHTTPGenerator}.
        assertFalse(result.isEmpty());

        //Check that all commands are marked as GET
        result.forEach(element -> assertEquals(RESTfulRPCModel.RESTfulRPCMethod.HTTPCommand.GET,
                element.getHTTPCommand()));

        //Check URIs
        result.forEach(element -> assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //Check return types are NOT void
        result.forEach(element -> assertNotEquals(void.class, element.getMethod().getReturnType()));

    }

    @Test
    public void validatePOSTCommands()
    {
        var defaultObject = new SimpleApplicationService(42);
        var objectUnderTest = new RESTfulRPCModel(defaultObject);

        var result = objectUnderTest.getPOSTCommands();

        //Check all conventions as defined in {@link RESTfulRPCGenerator}.
        assertFalse(result.isEmpty());

        //Check that all commands are marked as GET
        result.forEach(element -> assertEquals(RESTfulRPCModel.RESTfulRPCMethod.HTTPCommand.POST,
                element.getHTTPCommand()));

        //Check URIs
        result.forEach(element -> assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //Check return types are NOT void or Parameter > 0 
        result.forEach(element -> assertTrue( (void.class.equals(element.getMethod().getReturnType())
                                            || element.getMethod().getParameterCount() > 0 )));

    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidApplicationService()
    {
        //Act / Assert
        new RESTfulRPCModel(new UnsupportedApplicationService());

    }

}
