package io.ddd.jexxa.core.factory;


import java.util.ArrayList;
import java.util.Properties;

import io.ddd.jexxa.application.domainservice.IDefaultConstructorService;
import io.ddd.jexxa.application.domainservice.IFactoryMethodService;
import io.ddd.jexxa.application.domainservice.INotImplementedService;
import io.ddd.jexxa.application.domainservice.INotUniqueService;
import io.ddd.jexxa.application.domainservice.IPropertiesConstructorService;
import io.ddd.jexxa.application.infrastructure.drivenadapter.factory.DefaultConstructorAdapter;
import io.ddd.jexxa.application.infrastructure.drivenadapter.factory.PropertiesConstructorAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * @see AdapterFactory for conventions to create an adapter 
 */
public class AdapterFactoryTest
{
    private final String packageName = "io.ddd.jexxa";
    
    @Test
    public void createDrivenAdapter() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var result = objectUnderTest.newInstanceOf(IDefaultConstructorService.class);

        //Assert
        Assertions.assertNotNull(result);
    }

    @Test
    public void createDrivenAdapterImpl() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var firstResult = objectUnderTest.newInstanceOf(DefaultConstructorAdapter.class);
        var secondResult = objectUnderTest.newInstanceOf(PropertiesConstructorAdapter.class, new Properties());

        //Assert
        Assertions.assertNotNull(firstResult);
        Assertions.assertNotNull(secondResult);
    }


    @Test
    public void getDrivenAdapter() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var first = objectUnderTest.getInstanceOf(IDefaultConstructorService.class);
        var second = objectUnderTest.getInstanceOf(IDefaultConstructorService.class);


        //Act
        var firstProperties = objectUnderTest.getInstanceOf(IPropertiesConstructorService.class, new Properties());
        var secondProperties = objectUnderTest.getInstanceOf(IPropertiesConstructorService.class, new Properties());

        //Assert
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        Assertions.assertNotNull(firstProperties);
        Assertions.assertNotNull(secondProperties);

        Assertions.assertEquals(first,second);
        Assertions.assertEquals(firstProperties,secondProperties);
    }

    @Test
    public void createDrivenAdapterWithPropertiesConstructor() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        var properties = new Properties();

        //Act
        var result = objectUnderTest.newInstanceOf(IPropertiesConstructorService.class, properties);

        //Assert
        Assertions.assertNotNull(result);
    }


    @Test 
    public void createDrivenAdapterWithFactoryMethod() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var result = objectUnderTest.newInstanceOf(IFactoryMethodService.class);

        //Assert
        Assertions.assertNotNull(result);
    }

    @Test
    public void createDrivenAdapterWithPropertiesFactoryMethod() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
            whiteListPackage(packageName);
        var properties = new Properties();
        
        //Act
        var result = objectUnderTest.newInstanceOf(IFactoryMethodService.class, properties);

        //Assert
        Assertions.assertNotNull(result);
    }


    @Test
    public void drivenAdapterAvailable() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(IDefaultConstructorService.class);
        adapterList.add(IFactoryMethodService.class);
        adapterList.add(IPropertiesConstructorService.class);

        //Act
        boolean result = objectUnderTest.isAvailable(adapterList);

        //Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void drivenAdapterUnavailable() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(INotImplementedService.class);

        //Act
        boolean result = objectUnderTest.isAvailable(adapterList);

        //Assert
        Assertions.assertFalse(result);
    }

    @Test 
    public void createNoUniqueImplementation() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                objectUnderTest.newInstanceOf(INotUniqueService.class)
        );
    }

    @Test 
    public void createNoImplementationAvailable() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                objectUnderTest.newInstanceOf(INotImplementedService.class)
        );
    }

}
