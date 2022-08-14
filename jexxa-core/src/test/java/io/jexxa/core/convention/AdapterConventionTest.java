package io.jexxa.core.convention;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.infrastructure.drivenadapter.factory.DefaultConstructorServiceImpl;
import io.jexxa.application.infrastructure.drivenadapter.factory.FactoryMethodServiceImpl;
import io.jexxa.application.infrastructure.drivenadapter.factory.PropertiesConstructorServiceImpl;
import io.jexxa.application.infrastructure.drivingadapter.InvalidAdapter;
import io.jexxa.application.infrastructure.drivingadapter.messaging.SimpleApplicationServiceAdapter;
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
        AdapterConvention.validate(DefaultConstructorServiceImpl.class);

        AdapterConvention.validate(FactoryMethodServiceImpl.class);

        AdapterConvention.validate(PropertiesConstructorServiceImpl.class);

        assertThrows(AdapterConventionViolation.class, () -> AdapterConvention.validate(InvalidAdapter.class));
    }

    @Test
    void validatePortAdapterConvention()
    {
        //Arrange
        var infrastructure = List.of("io.jexxa.application.infrastructure.drivingadapter");

        //Assert all port adapter conventions
        assertTrue(AdapterConvention.isPortAdapter(SimpleApplicationServiceAdapter.class, infrastructure));
        assertFalse(AdapterConvention.isPortAdapter(SimpleApplicationService.class, infrastructure));
    }

}
