package io.jexxa.core.factory;


import java.util.Properties;

import io.jexxa.TestTags;
import io.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;
import io.jexxa.application.infrastructure.drivingadapter.messaging.SimpleApplicationServiceAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestTags.UNIT_TEST)
class PortFactoryTest
{

    private final String applicationCorePackageName = "io.jexxa.application";
    private final String drivenAdapterPackageName = "io.jexxa.application.infrastructure";


    @Test
    void adapterAvailable() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenAdapters.class);

        //Assert
        Assertions.assertTrue(result);
    }

    @Test
    void adapterUnavailable() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenAdapters.class);

        //Assert
        Assertions.assertTrue(result);
    }


    @Test
    void newInstanceOfPort() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());
        var second = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());

        //Assert
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertNotEquals(first, second);
    }

    @Test
    void getInstanceOfPort() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());
        var second = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());

        //Assert that first and second adapter are equal 
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertEquals(first,second);
    }


    @Test
    void getInstanceOfPortAdapter() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.getPortAdapterOf(SimpleApplicationServiceAdapter.class, new Properties());
        var second = objectUnderTest.getPortAdapterOf(SimpleApplicationServiceAdapter.class, new Properties());

        //Assert that first and second adapter are equal
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertEquals(first.getPort(),second.getPort());
    }

}