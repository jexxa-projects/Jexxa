package io.jexxa.core.factory;


import io.jexxa.TestConstants;
import io.jexxa.testapplication.domainservice.ValidDefaultConstructorService;
import io.jexxa.testapplication.domainservice.ValidFactoryMethodService;
import io.jexxa.testapplication.domainservice.NotImplementedService;
import io.jexxa.testapplication.domainservice.NotUniqueService;
import io.jexxa.testapplication.domainservice.ValidPropertiesConstructorService;
import io.jexxa.testapplication.domainservice.InvalidPropertiesService;
import io.jexxa.testapplication.infrastructure.drivenadapter.factory.ValidDefaultConstructorServiceImpl;
import io.jexxa.testapplication.infrastructure.drivenadapter.factory.ValidPropertiesConstructorServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.Properties;

import static io.jexxa.core.factory.PackageConstants.JEXXA_DRIVEN_ADAPTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @see AdapterFactory for conventions to create an adapter
 */
@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class AdapterFactoryTest
{
    static AdapterFactory objectUnderTest = null;

    @BeforeAll
    static void initFactory()
    {
        objectUnderTest = new AdapterFactory().
                acceptPackage(JEXXA_DRIVEN_ADAPTER);
    }

    @Test
    void createDrivenAdapter() {
        //Act
        var result = objectUnderTest.newInstanceOf(ValidDefaultConstructorService.class);

        //Assert
        assertNotNull(result);
    }

    @Test
    void createDrivenAdapterImpl() {
        //Act
        var firstResult = objectUnderTest.newInstanceOf(ValidDefaultConstructorServiceImpl.class);
        var secondResult = objectUnderTest.newInstanceOf(ValidPropertiesConstructorServiceImpl.class, new Properties());

        //Assert
        assertNotNull(firstResult);
        assertNotNull(secondResult);
    }


    @Test
    void validateSingletonScopeOfDrivenAdapter() {
        //Act
        var first = objectUnderTest.getInstanceOf(ValidDefaultConstructorService.class, new Properties());
        var second = objectUnderTest.getInstanceOf(ValidDefaultConstructorService.class, new Properties());


        //Act
        var firstProperties = objectUnderTest.getInstanceOf(ValidPropertiesConstructorService.class, new Properties());
        var secondProperties = objectUnderTest.getInstanceOf(ValidPropertiesConstructorService.class, new Properties());

        //Assert
        assertNotNull(first);
        assertNotNull(second);
        assertNotNull(firstProperties);
        assertNotNull(secondProperties);

        assertEquals(first,second);
        assertEquals(firstProperties,secondProperties);
    }

    @Test
    void createDrivenAdapterWithPropertiesConstructor() {
        //Act
        var result = objectUnderTest.newInstanceOf(ValidPropertiesConstructorService.class, new Properties());

        //Assert
        assertNotNull(result);
    }


    @Test
    void createDrivenAdapterWithFactoryMethod() {
        //Act
        var result = objectUnderTest.newInstanceOf(ValidFactoryMethodService.class);

        //Assert
        assertNotNull(result);
    }

    @Test
    void createDrivenAdapterWithPropertiesFactoryMethod() {
        //Act
        var result = objectUnderTest.newInstanceOf(ValidFactoryMethodService.class, new Properties());

        //Assert
        assertNotNull(result);
    }


    @Test
    void drivenAdapterAvailable() {
        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(ValidDefaultConstructorService.class);
        adapterList.add(ValidFactoryMethodService.class);
        adapterList.add(ValidPropertiesConstructorService.class);

        //Act
        boolean result = objectUnderTest.isAvailable(adapterList);

        //Assert
        assertTrue(result);
    }

    @Test
    void drivenAdapterUnavailable() {
        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(NotImplementedService.class);

        //Act
        boolean result = objectUnderTest.isAvailable(adapterList);

        //Assert
        assertFalse(result);
    }

    @Test
    void createNoUniqueImplementation() {
        //Act/Assert
        var exception = assertThrows(AmbiguousAdapterException.class, () ->
                objectUnderTest.newInstanceOf(NotUniqueService.class)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    void createNoImplementationAvailable() {
        //Act/Assert
        assertThrows(IllegalArgumentException.class, () ->
                objectUnderTest.newInstanceOf(NotImplementedService.class)
        );
    }


    @Test
    void createInvalidAdapterProperties() {
        //Arrange
        var properties = new Properties();

        //Act/Assert
        assertThrows(InvalidAdapterException.class, () ->
                objectUnderTest.newInstanceOf(InvalidPropertiesService.class, properties)
        );
    }
}
