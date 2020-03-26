package io.ddd.jexxa.core;


import java.util.Properties;

import io.ddd.jexxa.applicationcore.applicationservice.ApplicationServiceWithDrivenApdapters;
import io.ddd.jexxa.applicationcore.domainservice.IFactroyMethodService;
import io.ddd.jexxa.applicationcore.domainservice.INotUniqueService;
import io.ddd.jexxa.applicationcore.domainservice.IDefaultConstructorService;
import io.ddd.jexxa.applicationcore.domainservice.INotImplementedService;
import io.ddd.jexxa.applicationcore.domainservice.IPropertiesConstructorService;
import org.junit.Assert;
import org.junit.Test;

/*
 * Rules for creating a driving adapter:
 * 1. Public Default constructor available
 * 2. Public constructor with Properties as attribute
 * 3. Public static method with return type if the requested interface
 * 4. Public static method with return type if the requested interface and Properties as argument  
 */
public class DrivenAdapterFactoryTest
{
    @Test
    public void createDrivenAdapter() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();

        //Act
        var result = objectUnderTest.create(IDefaultConstructorService.class);

        //Assert
        Assert.assertNotNull(result);
    }

    @Test
    public void createDrivenAdapterWithPropertiesConstructor() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();
        var properties = new Properties();

        //Act
        var result = objectUnderTest.create(IPropertiesConstructorService.class, properties);

        //Assert
        Assert.assertNotNull(result);
    }


    @Test 
    public void createDrivenAdapterWithFactoryMethod() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();

        //Act
        var result = objectUnderTest.create(IFactroyMethodService.class);

        //Assert
        Assert.assertNotNull(result);
    }

    @Test
    public void createDrivenAdapterWithPropertiesFactoryMethod() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();
        var properties = new Properties();
        //Act
        var result = objectUnderTest.create(IFactroyMethodService.class, properties);

        //Assert
        Assert.assertNotNull(result);
    }


    @Test
    public void drivenAdapterAvailable() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();

        //Act
        boolean result = objectUnderTest.adaptersAvailable(ApplicationServiceWithDrivenApdapters.class);

        //Assert
        Assert.assertTrue(result);
    }

    @Test
    public void drivenAdapterUnavailable() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();

        //Act
        boolean result = objectUnderTest.adaptersAvailable(ApplicationServiceWithDrivenApdapters.class);

        //Assert
        Assert.assertTrue(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNoUniqueImplementation() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();

        //Act
        objectUnderTest.create(INotUniqueService.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNoImplementationAvailable() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory();

        //Act
        objectUnderTest.create(INotImplementedService.class);
    }

}
