package io.jexxa.core.convention;

import io.jexxa.testapplication.applicationservice.SimpleApplicationService;
import io.jexxa.testapplication.infrastructure.drivenadapter.factory.ValidDefaultConstructorServiceImpl;
import io.jexxa.testapplication.infrastructure.drivenadapter.factory.ValidFactoryMethodServiceImpl;
import io.jexxa.testapplication.infrastructure.drivenadapter.factory.ValidPropertiesConstructorServiceImpl;
import io.jexxa.testapplication.infrastructure.drivingadapter.generic.InvalidDrivingAdapter;
import io.jexxa.testapplication.infrastructure.drivingadapter.portadapter.PortAdapter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdapterConventionTest
{
    @Test
    void validateAdapterConvention()
    {
        //Arrange - Nothing

        //Assert all Adapter conventions
        AdapterConvention.validate(ValidDefaultConstructorServiceImpl.class);

        AdapterConvention.validate(ValidFactoryMethodServiceImpl.class);

        AdapterConvention.validate(ValidPropertiesConstructorServiceImpl.class);

        assertThrows(AdapterConventionViolation.class, () -> AdapterConvention.validate(InvalidDrivingAdapter.class));
    }

    @Test
    void validatePortAdapterConvention()
    {
        //Arrange
        var infrastructure = List.of("io.jexxa.testapplication.infrastructure.drivingadapter");

        //Assert all port adapter conventions
        assertTrue(AdapterConvention.isPortAdapter(PortAdapter.class, infrastructure));
        assertFalse(AdapterConvention.isPortAdapter(SimpleApplicationService.class, infrastructure));
    }

}
