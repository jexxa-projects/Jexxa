package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.applicationservice.UnsupportedApplicationService;
import org.junit.Test;

public class RESTfulRPCGeneratorTest
{

    @Test
    public void validateGETCommands()
    {
        var defaultObject = new SimpleApplicationService(42);
        var objectUnderTest = new RESTfulRPCGenerator(defaultObject);

        var result = objectUnderTest.getGETCommands();
        
        //Check all conventions as defined in {@link RESTfulHTTPGenerator}.
        assertFalse(result.isEmpty());

        //Check that all commands are marked as GET
        result.forEach(element -> assertEquals(RESTfulRPCGenerator.RESTfulRPC.HTTPCommand.GET,
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
        var objectUnderTest = new RESTfulRPCGenerator(defaultObject);

        var result = objectUnderTest.getPOSTCommands();

        //Check all conventions as defined in {@link RESTfulRPCGenerator}.
        assertFalse(result.isEmpty());

        //Check that all commands are marked as GET
        result.forEach(element -> assertEquals(RESTfulRPCGenerator.RESTfulRPC.HTTPCommand.POST,
                element.getHTTPCommand()));

        //Check URIs
        result.forEach(element -> assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.getMethod().getName(),
                element.getResourcePath()));

        //Check return types are NOT void
        result.forEach(element -> assertEquals(void.class, element.getMethod().getReturnType()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void invlalidApplicationService()
    {
        //Arrange
        var unsupportedApplicationService = new UnsupportedApplicationService();

        //Act
        new RESTfulRPCGenerator(unsupportedApplicationService);

    }

}
