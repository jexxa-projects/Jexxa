package io.jexxa.core.convention;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.infrastructure.drivenadapter.factory.ValidDefaultConstructorServiceImpl;
import io.jexxa.application.infrastructure.drivenadapter.factory.ValidFactoryMethodServiceImpl;
import io.jexxa.application.infrastructure.drivenadapter.factory.ValidPropertiesConstructorServiceImpl;
import io.jexxa.application.infrastructure.drivingadapter.generic.InvalidDrivingAdapter;
import io.jexxa.application.infrastructure.drivingadapter.portadapter.PortAdapter;
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
        var infrastructure = List.of("io.jexxa.application.infrastructure.drivingadapter");

        //Assert all port adapter conventions
        assertTrue(AdapterConvention.isPortAdapter(PortAdapter.class, infrastructure));
        assertFalse(AdapterConvention.isPortAdapter(SimpleApplicationService.class, infrastructure));
    }

}
