package io.jexxa.infrastructure.drivingadapter.rest;

import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class RESTfulRPCConventionTest
{
    private RESTfulRPCConvention objectUnderTest;
    private SimpleApplicationService simpleApplicationService;

    @BeforeEach
    void setupTests()
    {
        simpleApplicationService = new SimpleApplicationService();
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
        result.forEach(element -> assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.method().getName(),
                element.resourcePath()));

        //4. Check return types are NOT void
        result.forEach(element -> assertNotEquals(void.class, element.method().getReturnType()));
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
        result.forEach(element -> assertEquals("/" + SimpleApplicationService.class.getSimpleName() + "/"+element.method().getName(),
                element.resourcePath()));

        //4.Check return types are NOT void or Parameter > 0
        result.forEach(element -> assertTrue( (void.class.equals(element.method().getReturnType())
                                            || element.method().getParameterCount() > 0 )));

    }

    @Test
    void noStaticMethods()
    {
        //Arrange
        var staticMethods = Arrays.stream(simpleApplicationService.getClass().getMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .toList();

        //Act - get All methods
        var methods = new ArrayList<RESTfulRPCConvention.RESTfulRPCMethod>();
        methods.addAll(objectUnderTest.getPOSTCommands());
        methods.addAll(objectUnderTest.getGETCommands());

        //Assert - that we get mbean methods without static methods
        assertNotNull(methods);
        assertTrue ( staticMethods.stream().allMatch(
                staticMethod -> methods.stream()
                        .noneMatch( method -> method.method().getName().equals(staticMethod.getName()))
                )
        );
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
