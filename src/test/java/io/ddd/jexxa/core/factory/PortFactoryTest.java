package io.ddd.jexxa.core.factory;

import java.util.Properties;

import io.ddd.jexxa.dummyapplication.applicationservice.ApplicationWrapper;
import io.ddd.jexxa.dummyapplication.applicationservice.ApplicationServiceWithDrivenApdapters;
import org.junit.Assert;
import org.junit.Test;

public class PortFactoryTest
{

    private String applicationCorePackageName = "io.ddd.jexxa.dummyapplication";
    private String drivenAdapterPackageName = "io.ddd.jexxa.dummyapplication.infrastructure";


    @Test
    public void adapterAvailable() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenApdapters.class);

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
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenApdapters.class);

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
        var first = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenApdapters.class, new Properties());
        var second = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenApdapters.class, new Properties());

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
        var first = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenApdapters.class, new Properties());
        var second = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenApdapters.class, new Properties());

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
        var first = objectUnderTest.getWrappedInstanceOf(ApplicationWrapper.class, new Properties());
        var second = objectUnderTest.getWrappedInstanceOf(ApplicationWrapper.class, new Properties());

        //Assert that first and second adapter are equal
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals(first.getPort(),second.getPort());
    }

}