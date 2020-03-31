package io.ddd.jexxa.core.factory;


import java.util.ArrayList;
import java.util.Properties;

import io.ddd.jexxa.dummyapplication.domainservice.IDefaultConstructorService;
import io.ddd.jexxa.dummyapplication.domainservice.IFactroyMethodService;
import io.ddd.jexxa.dummyapplication.domainservice.INotImplementedService;
import io.ddd.jexxa.dummyapplication.domainservice.INotUniqueService;
import io.ddd.jexxa.dummyapplication.domainservice.IPropertiesConstructorService;
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
    private String packageName = "io.ddd.jexxa";
    
    @Test
    public void createDrivenAdapter() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
                whiteListPackage(packageName);

        //Act
        var result = objectUnderTest.newInstanceOfInterface(IDefaultConstructorService.class);

        //Assert
        Assert.assertNotNull(result);
    }


    @Test
    public void getDrivenAdapter() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
                whiteListPackage(packageName);

        //Act
        var first = objectUnderTest.getInstanceOfInterface(IDefaultConstructorService.class);
        var second = objectUnderTest.getInstanceOfInterface(IDefaultConstructorService.class);

        //Assert
        Assert.assertNotNull(first);
        Assert.assertNotNull(second);
        Assert.assertEquals(first,second);
    }

    @Test
    public void createDrivenAdapterWithPropertiesConstructor() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
                whiteListPackage(packageName);

        var properties = new Properties();

        //Act
        var result = objectUnderTest.newInstanceOfInterface(IPropertiesConstructorService.class, properties);

        //Assert
        Assert.assertNotNull(result);
    }


    @Test 
    public void createDrivenAdapterWithFactoryMethod() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
                whiteListPackage(packageName);

        //Act
        var result = objectUnderTest.newInstanceOfInterface(IFactroyMethodService.class);

        //Assert
        Assert.assertNotNull(result);
    }

    @Test
    public void createDrivenAdapterWithPropertiesFactoryMethod() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
            whiteListPackage(packageName);
        var properties = new Properties();
        
        //Act
        var result = objectUnderTest.newInstanceOfInterface(IFactroyMethodService.class, properties);

        //Assert
        Assert.assertNotNull(result);
    }


    @Test
    public void drivenAdapterAvailable() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
                whiteListPackage(packageName);

        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(IDefaultConstructorService.class);
        adapterList.add(IFactroyMethodService.class);
        adapterList.add(IPropertiesConstructorService.class);

        //Act
        boolean result = objectUnderTest.isAvailable(adapterList);

        //Assert
        Assert.assertTrue(result);
    }

    @Test
    public void drivenAdapterUnavailable() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
                whiteListPackage(packageName);

        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(INotImplementedService.class);

        //Act
        boolean result = objectUnderTest.isAvailable(adapterList);

        //Assert
        Assert.assertFalse(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNoUniqueImplementation() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
                whiteListPackage(packageName);

        //Act
        objectUnderTest.newInstanceOfInterface(INotUniqueService.class);
    }

    @Test (expected = IllegalArgumentException.class)
    public void createNoImplementationAvailable() {
        //Arrange
        var objectUnderTest = new DrivenAdapterFactory().
                whiteListPackage(packageName);

        //Act
        objectUnderTest.newInstanceOfInterface(INotImplementedService.class);
    }

}
