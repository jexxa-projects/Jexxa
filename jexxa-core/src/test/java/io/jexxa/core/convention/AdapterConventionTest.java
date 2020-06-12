package io.jexxa.core.convention;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        //Assert all Adapter conventions
        AdapterConvention.validate(DefaultConstructorAdapter.class);

        AdapterConvention.validate(FactoryMethodAdapter.class);

        AdapterConvention.validate(PropertiesConstructorAdapter.class);

        assertThrows(AdapterConventionViolation.class, () -> AdapterConvention.validate(InvalidAdapter.class));
    }

    @Test
    void validatePortAdapterConvention()
    {
        //Assert all port adapter conventions
        assertTrue(AdapterConvention.isPortAdapter(SimpleApplicationServiceAdapter.class));
        assertFalse(AdapterConvention.isPortAdapter(SimpleApplicationService.class));
    }

}
