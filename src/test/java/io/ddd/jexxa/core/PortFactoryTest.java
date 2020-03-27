package io.ddd.jexxa.core;

import io.ddd.jexxa.applicationcore.applicationservice.ApplicationServiceWithDrivenApdapters;
import org.junit.Assert;
import org.junit.Test;

public class PortFactoryTest
{

    private String applicationCorePackageName = "io.ddd.jexxa.applicationcore";
    private String drivenAdapterPackageName = "io.ddd.jexxa.infrastructure";


    @Test
    public void drivenAdapterAvailable() {
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
    public void drivenAdapterUnavailable() {
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

}