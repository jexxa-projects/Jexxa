package io.jexxa.core.convention;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.application.infrastructure.drivenadapter.factory.DefaultConstructorAdapter;
import io.jexxa.application.infrastructure.drivenadapter.factory.FactoryMethodAdapter;
import io.jexxa.application.infrastructure.drivenadapter.factory.PropertiesConstructorAdapter;
import io.jexxa.application.infrastructure.drivingadapter.InvalidAdapter;
import io.jexxa.application.infrastructure.drivingadapter.messaging.SimpleApplicationServiceAdapter;
import org.junit.jupiter.api.Test;

class AdapterConventionTest
{
    @Test
    void validateAdapterConvention()
    {
        //Arrange
        var acceptedInfrastructure = List.of("io.jexxa.application.infrastructure.drivenadapter");

        //Assert all Adapter conventions
        AdapterConvention.validate(DefaultConstructorAdapter.class, acceptedInfrastructure);

        AdapterConvention.validate(FactoryMethodAdapter.class, acceptedInfrastructure);

        AdapterConvention.validate(PropertiesConstructorAdapter.class, acceptedInfrastructure);

        assertThrows(AdapterConventionViolation.class, () -> AdapterConvention.validate(InvalidAdapter.class, acceptedInfrastructure));
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
