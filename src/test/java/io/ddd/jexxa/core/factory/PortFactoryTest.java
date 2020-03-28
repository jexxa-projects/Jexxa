package io.ddd.jexxa.core.factory;

import java.util.Properties;

import io.ddd.jexxa.dummyapplication.applicationservice.ApplicationServiceWithDrivenApdapters;
import org.junit.Assert;
import org.junit.Test;

public class PortFactoryTest
{

    private String applicationCorePackageName = "io.ddd.jexxa.dummyapplication";
    private String drivenAdapterPackageName = "io.ddd.jexxa.dummyapplication.infrastructure";


    @Test
    public void drivenAdapterAvailable() {
        //Arrange
        var drivenAdapterFactory = new DrivenAdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenApdapters.class);

        //Assert
        Assert.assertTrue(result);
    }

    @Test
    public void drivenAdapterUnavailable() {
        //Arrange
        var drivenAdapterFactory = new DrivenAdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenApdapters.class);

        //Assert
        Assert.assertTrue(result);
    }


    @Test
    public void createPort() {
        //Arrange
        var drivenAdapterFactory = new DrivenAdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var result = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenApdapters.class, new Properties());

        //Assert
        Assert.assertNotNull(result);
    }

}