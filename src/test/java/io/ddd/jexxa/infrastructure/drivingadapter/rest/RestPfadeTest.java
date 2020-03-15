package io.ddd.jexxa.infrastructure.drivingadapter.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;
import org.junit.Test;

public class RestPfadeTest
{

    @Test
    public void printPath()
    {

        List<URIGenerator.RestURL> restURL = URIGenerator.getRestURLs(SimpleApplicationService.class);
        
        //Check that there are URLs included stating name of unit under test
        assertTrue(restURL
                .stream()
                .anyMatch(element -> element.getRestURL().contains("/"+SimpleApplicationService.class.getSimpleName()+"/")));

        //Check that no methods from base class are included  
        assertFalse(restURL
                .stream()
                .anyMatch(element -> element.getRestURL().contains("/"+Object.class.getSimpleName()+"/")));
    }
}
