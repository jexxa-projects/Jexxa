package io.jexxa.core.factory;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Properties;

import io.jexxa.TestConstants;
import io.jexxa.application.domainservice.IDefaultConstructorService;
import io.jexxa.application.domainservice.IFactoryMethodService;
import io.jexxa.application.domainservice.INotImplementedService;
import io.jexxa.application.domainservice.INotUniqueService;
import io.jexxa.application.domainservice.IPropertiesConstructorService;
import io.jexxa.application.infrastructure.drivenadapter.factory.DefaultConstructorAdapter;
import io.jexxa.application.infrastructure.drivenadapter.factory.PropertiesConstructorAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * @see AdapterFactory for conventions to create an adapter 
 */
@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class AdapterFactoryTest
{
    private final String packageName = "io.jexxa.application.infrastructure";
    
    @Test
    protected void createDrivenAdapter() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var result = objectUnderTest.newInstanceOf(IDefaultConstructorService.class);

        //Assert
        assertNotNull(result);
    }

    @Test
    protected void createDrivenAdapterImpl() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var firstResult = objectUnderTest.newInstanceOf(DefaultConstructorAdapter.class);
        var secondResult = objectUnderTest.newInstanceOf(PropertiesConstructorAdapter.class, new Properties());

        //Assert
        assertNotNull(firstResult);
        assertNotNull(secondResult);
    }


    @Test
    protected void getDrivenAdapter() {
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
        assertNotNull(first);
        assertNotNull(second);
        assertNotNull(firstProperties);
        assertNotNull(secondProperties);

        assertEquals(first,second);
        assertEquals(firstProperties,secondProperties);
    }

    @Test
    protected void createDrivenAdapterWithPropertiesConstructor() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        var properties = new Properties();

        //Act
        var result = objectUnderTest.newInstanceOf(IPropertiesConstructorService.class, properties);

        //Assert
        assertNotNull(result);
    }


    @Test
    protected void createDrivenAdapterWithFactoryMethod() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act
        var result = objectUnderTest.newInstanceOf(IFactoryMethodService.class);

        //Assert
        assertNotNull(result);
    }

    @Test
    protected void createDrivenAdapterWithPropertiesFactoryMethod() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
            whiteListPackage(packageName);
        var properties = new Properties();
        
        //Act
        var result = objectUnderTest.newInstanceOf(IFactoryMethodService.class, properties);

        //Assert
        assertNotNull(result);
    }


    @Test
    protected void drivenAdapterAvailable() {
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
        assertTrue(result);
    }

    @Test
    protected void drivenAdapterUnavailable() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(INotImplementedService.class);

        //Act
        boolean result = objectUnderTest.isAvailable(adapterList);

        //Assert
        assertFalse(result);
    }

    @Test
    protected void createNoUniqueImplementation() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act/Assert
        assertThrows(IllegalArgumentException.class, () ->
                objectUnderTest.newInstanceOf(INotUniqueService.class)
        );
    }

    @Test
    protected void createNoImplementationAvailable() {
        //Arrange
        var objectUnderTest = new AdapterFactory().
                whiteListPackage(packageName);

        //Act/Assert
        assertThrows(IllegalArgumentException.class, () ->
                objectUnderTest.newInstanceOf(INotImplementedService.class)
        );
    }
}
