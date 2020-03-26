package io.ddd.jexxa.core;

import static org.junit.Assert.*;

import io.ddd.jexxa.applicationcore.applicationservice.ApplicationServiceWithDrivenApdapters;
import org.junit.Assert;
import org.junit.Test;

public class ApplicationCoreFactoryTest {

    private String applicationCorePackageName = "io.ddd.jexxa.applicationcore";
    private String drivenAdapterPackageName = "io.ddd.jexxa.infrastructure";


    @Test
    public void drivenAdapterAvailable() {
        //Arrange
        var drivenAdapterFactory = new DrivenAdapterFactory().
                whiteListPackage(drivenAdapterPackageName);
        var objectUnderTest = new ApplicationCoreFactory(drivenAdapterFactory).
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
        var objectUnderTest = new ApplicationCoreFactory(drivenAdapterFactory).
                whiteListPackage(applicationCorePackageName);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenApdapters.class);

        //Assert
        Assert.assertTrue(result);
    }

}