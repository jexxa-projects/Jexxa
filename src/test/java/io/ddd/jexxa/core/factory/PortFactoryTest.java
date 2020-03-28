package io.ddd.jexxa.core.factory;

import java.util.Properties;

import io.ddd.jexxa.applicationcore.applicationservice.ApplicationServiceWithDrivenApdapters;
import io.ddd.jexxa.core.factory.AdapterFactory;
import io.ddd.jexxa.core.factory.PortFactory;
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


    @Test
    public void createPort() {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        var result = objectUnderTest.createByType(ApplicationServiceWithDrivenApdapters.class, new Properties());

        //Assert
        Assert.assertNotNull(result);
    }

}