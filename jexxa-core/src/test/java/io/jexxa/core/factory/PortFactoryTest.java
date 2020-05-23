package io.jexxa.core.factory;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;
import io.jexxa.application.infrastructure.drivingadapter.messaging.SimpleApplicationServiceAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class PortFactoryTest
{

    private final String applicationCorePackageName = "io.jexxa.application";
    private final String drivenAdapterPackageName = "io.jexxa.application.infrastructure";


    @Test
    protected void adapterAvailable() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenAdapters.class);

        //Assert
        assertTrue(result);
    }

    @Test
    protected void adapterUnavailable() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenAdapters.class);

        //Assert
        assertTrue(result);
    }


    @Test
    protected void newInstanceOfPort() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());
        var second = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());

        //Assert
        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first, second);
    }

    @Test
    protected void getInstanceOfPort() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());
        var second = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());

        //Assert that first and second adapter are equal 
        assertNotNull(first);
        assertNotNull(second);
        assertEquals(first,second);
    }


    @Test
    protected void getInstanceOfPortAdapter() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.getPortAdapterOf(SimpleApplicationServiceAdapter.class, new Properties());
        var second = objectUnderTest.getPortAdapterOf(SimpleApplicationServiceAdapter.class, new Properties());

        //Assert that first and second adapter are equal
        assertNotNull(first);
        assertNotNull(second);
        assertEquals(first.getPort(),second.getPort());
    }

}