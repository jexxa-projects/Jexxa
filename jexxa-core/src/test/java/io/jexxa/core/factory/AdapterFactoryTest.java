package io.jexxa.core.factory;


import io.jexxa.TestConstants;
import io.jexxa.application.domainservice.*;
import io.jexxa.application.infrastructure.drivenadapter.factory.DefaultConstructorAdapter;
import io.jexxa.application.infrastructure.drivenadapter.factory.PropertiesConstructorAdapter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.Properties;

import static io.jexxa.TestConstants.JEXXA_DRIVEN_ADAPTER;
import static org.junit.jupiter.api.Assertions.*;

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
        var result = objectUnderTest.newInstanceOf(IDefaultConstructorService.class);

        //Assert
        assertNotNull(result);
    }

    @Test
    void createDrivenAdapterImpl() {
        //Act
        var firstResult = objectUnderTest.newInstanceOf(DefaultConstructorAdapter.class);
        var secondResult = objectUnderTest.newInstanceOf(PropertiesConstructorAdapter.class, new Properties());

        //Assert
        assertNotNull(firstResult);
        assertNotNull(secondResult);
    }


    @Test
    void validateSingletonScopeOfDrivenAdapter() {
        //Act
        var first = objectUnderTest.getInstanceOf(IDefaultConstructorService.class, new Properties());
        var second = objectUnderTest.getInstanceOf(IDefaultConstructorService.class, new Properties());


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
    void createDrivenAdapterWithPropertiesConstructor() {
        //Act
        var result = objectUnderTest.newInstanceOf(IPropertiesConstructorService.class, new Properties());

        //Assert
        assertNotNull(result);
    }


    @Test
    void createDrivenAdapterWithFactoryMethod() {
        //Act
        var result = objectUnderTest.newInstanceOf(IFactoryMethodService.class);

        //Assert
        assertNotNull(result);
    }

    @Test
    void createDrivenAdapterWithPropertiesFactoryMethod() {
        //Act
        var result = objectUnderTest.newInstanceOf(IFactoryMethodService.class, new Properties());

        //Assert
        assertNotNull(result);
    }


    @Test
    void drivenAdapterAvailable() {
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
    void drivenAdapterUnavailable() {
        var adapterList = new ArrayList<Class<?>>();
        adapterList.add(INotImplementedService.class);

        //Act
        boolean result = objectUnderTest.isAvailable(adapterList);

        //Assert
        assertFalse(result);
    }

    @Test
    void createNoUniqueImplementation() {
        //Act/Assert
        var exception = assertThrows(AmbiguousAdapterException.class, () ->
                objectUnderTest.newInstanceOf(INotUniqueService.class)
        );
        assertNotNull(exception.getMessage());
    }

    @Test
    void createNoImplementationAvailable() {
        //Act/Assert
        assertThrows(IllegalArgumentException.class, () ->
                objectUnderTest.newInstanceOf(INotImplementedService.class)
        );
    }


    @Test
    void createInvalidAdapterProperties() {
        //Arrange
        var properties = new Properties();

        //Act/Assert
        assertThrows(InvalidAdapterException.class, () ->
                objectUnderTest.newInstanceOf(IInvalidAdapterProperties.class, properties)
        );
    }
}
