package io.jexxa.core.factory;


import io.jexxa.testapplication.applicationservice.ApplicationServiceWithDrivenAdapters;
import io.jexxa.testapplication.applicationservice.ApplicationServiceWithInvalidDrivenAdapters;
import io.jexxa.testapplication.infrastructure.drivingadapter.portadapter.PortAdapter;
import io.jexxa.testapplication.infrastructure.drivingadapter.portadapter.PortAdapterWithProperties;
import io.jexxa.testapplication.infrastructure.drivingadapter.portadapter.ThrowingPortAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Properties;

import static io.jexxa.TestConstants.UNIT_TEST;
import static io.jexxa.core.factory.PackageConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.core.factory.PackageConstants.JEXXA_DOMAIN_SERVICE;
import static io.jexxa.core.factory.PackageConstants.JEXXA_DRIVEN_ADAPTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Execution(ExecutionMode.CONCURRENT)
@Tag(UNIT_TEST)
class PortFactoryTest
{
    @Test
    void createPortWithAvailableDrivenAdapter()
    {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                acceptPackage(JEXXA_DRIVEN_ADAPTER);
        var objectUnderTest = new PortFactory(drivenAdapterFactory)
                .acceptPackage(JEXXA_APPLICATION_SERVICE);

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenAdapters.class);

        //Assert
        assertTrue(result);
    }

    @Test
    void createPortWithMissingAdapter()
    {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                acceptPackage("invalid.package");
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                acceptPackage(JEXXA_APPLICATION_SERVICE);
        var properties = new Properties();

        //Act
        boolean result = objectUnderTest.isAvailable(ApplicationServiceWithDrivenAdapters.class);

        //Assert
        assertFalse(result);
        assertThrows(MissingAdapterException.class, () -> objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, properties));
    }

    @Test
    void createPortWithInvalidDrivenAdapter()
    {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                acceptPackage(JEXXA_DRIVEN_ADAPTER).acceptPackage(JEXXA_DOMAIN_SERVICE);
        var objectUnderTest = new PortFactory(drivenAdapterFactory)
                .acceptPackage(JEXXA_APPLICATION_SERVICE)
                .acceptPackage(JEXXA_DOMAIN_SERVICE);

        var properties = new Properties();

        //Act - Assert
        assertThrows(InvalidAdapterException.class,
                () -> objectUnderTest.getInstanceOf(ApplicationServiceWithInvalidDrivenAdapters.class, properties));
    }

    @Test
    void newInstanceOfPort()
    {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                acceptPackage(JEXXA_DRIVEN_ADAPTER);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                acceptPackage(JEXXA_APPLICATION_SERVICE);

        //Act
        var first = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());
        var second = objectUnderTest.newInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());

        //Assert
        assertNotNull(first);
        assertNotNull(second);
        assertNotEquals(first, second);
    }

    @Test
    void getInstanceOfPort()
    {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                acceptPackage(JEXXA_DRIVEN_ADAPTER);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                acceptPackage(JEXXA_APPLICATION_SERVICE);

        //Act
        var first = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());
        var second = objectUnderTest.getInstanceOf(ApplicationServiceWithDrivenAdapters.class, new Properties());

        //Assert that the first and second adapter is equal
        assertNotNull(first);
        assertNotNull(second);
        assertEquals(first, second);
    }


    @Test
    void getPortAdapter()
    {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                acceptPackage(JEXXA_DRIVEN_ADAPTER);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                acceptPackage(JEXXA_APPLICATION_SERVICE);

        //Act
        var first = objectUnderTest.getPortAdapterOf(PortAdapter.class, new Properties());
        var second = objectUnderTest.getPortAdapterOf(PortAdapter.class, new Properties());

        //Assert that first and second adapter is equal
        assertNotNull(first);
        assertNotNull(second);
        assertEquals(first.getPort(), second.getPort());
    }

    @Test
    void getInvalidPortAdapter()
    {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                acceptPackage(JEXXA_DRIVEN_ADAPTER);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                acceptPackage(JEXXA_APPLICATION_SERVICE);

        //Act / Assert
        var exception = assertThrows(PortFactory.InvalidPortConfigurationException.class,
                () -> objectUnderTest.newPortAdapterOf(ThrowingPortAdapter.class, new Properties()) );

        assertNotNull(exception.getMessage());
    }

    @Test
    void getPortAdapterWithProperties()
    {
        //Arrange
        var drivenAdapterFactory = new AdapterFactory().
                acceptPackage(JEXXA_DRIVEN_ADAPTER);
        var objectUnderTest = new PortFactory(drivenAdapterFactory).
                acceptPackage(JEXXA_APPLICATION_SERVICE);

        //Act / Assert
        var result = objectUnderTest.getPortAdapterOf(PortAdapterWithProperties.class, new Properties());

        assertNotNull(result);
    }
}