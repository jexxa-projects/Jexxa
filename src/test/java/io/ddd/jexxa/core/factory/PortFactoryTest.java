package io.ddd.jexxa.core.factory;

import java.util.Properties;

import io.ddd.jexxa.application.infrastructure.drivingadapter.messaging.SimpleApplicationServiceWrapper;
import io.ddd.jexxa.application.applicationservice.ApplicationServiceWithDrivenAdapters;
import org.junit.Assert;
import org.junit.Test;

public class PortFactoryTest
{

    private final String applicationCorePackageName = "io.ddd.jexxa.application";
    private final String drivenAdapterPackageName = "io.ddd.jexxa.application.infrastructure";


    @Test
    public void adapterAvailable() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenAdapters.class);

        //Assert
        Assert.assertTrue(result);
    }

    @Test
    public void adapterUnavailable() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenAdapters.class);

        //Assert
        Assert.assertTrue(result);
    }


    @Test
    public void newInstanceOfPort() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());
        var second = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());

        //Assert
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertNotEquals(first, second);
    }

    @Test
    public void getInstanceOfPort() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());
        var second = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());

        //Assert that first and second adapter are equal 
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals(first,second);
    }


    @Test
    public void getInstanceOfPortAdapter() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var first = objectUnderTest.getPortAdapterOf(SimpleApplicationServiceWrapper.class, new Properties());
        var second = objectUnderTest.getPortAdapterOf(SimpleApplicationServiceWrapper.class, new Properties());

        //Assert that first and second adapter are equal
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals(first.getPort(),second.getPort());
    }

}