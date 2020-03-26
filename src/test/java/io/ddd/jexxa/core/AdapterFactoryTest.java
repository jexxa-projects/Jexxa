package io.ddd.jexxa.core;


import java.util.ArrayList;
import java.util.Properties;

import io.ddd.jexxa.applicationcore.domainservice.IDefaultConstructorService;
import io.ddd.jexxa.applicationcore.domainservice.IFactroyMethodService;
import io.ddd.jexxa.applicationcore.domainservice.INotImplementedService;
import io.ddd.jexxa.applicationcore.domainservice.INotUniqueService;
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
public class AdapterFactoryTest
{
    private String packageName = "io.ddd.jexxa";
    
    @Test
    public void createDrivenAdapter() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var result = objectUnderTest.create(IDefaultConstructorService.class);

        //Assert
        Assert.assertNotNull(result);
    }

    @Test
    public void createDrivenAdapterWithPropertiesConstructor() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        var properties = new Properties();

        //Act
        var result = objectUnderTest.create(IPropertiesConstructorService.class, properties);

        //Assert
        Assert.assertNotNull(result);
    }


    @Test 
    public void createDrivenAdapterWithFactoryMethod() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var result = objectUnderTest.create(IFactroyMethodService.class);

        //Assert
        Assert.assertNotNull(result);
    }

    @Test
    public void createDrivenAdapterWithPropertiesFactoryMethod() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
            whiteListPackage(packageName);
        var properties = new Properties();
        
        //Act
        var result = objectUnderTest.create(IFactroyMethodService.class, properties);

        //Assert
        Assert.assertNotNull(result);
    }


    @Test
    public void drivenAdapterAvailable() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(IDefaultConstructorService.class);
        adapterList.add(IFactroyMethodService.class);
        adapterList.add(IPropertiesConstructorService.class);

        //Act
        boolean result = objectUnderTest.validateAdaptersAvailable(adapterList);

        //Assert
        Assert.assertTrue(result);
    }

    @Test
    public void drivenAdapterUnavailable() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(INotImplementedService.class);

        //Act
        boolean result = objectUnderTest.validateAdaptersAvailable(adapterList);

        //Assert
        Assert.assertFalse(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNoUniqueImplementation() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        objectUnderTest.create(INotUniqueService.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNoImplementationAvailable() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        objectUnderTest.create(INotImplementedService.class);
    }

}
