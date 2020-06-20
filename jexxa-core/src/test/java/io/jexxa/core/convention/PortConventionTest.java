package io.jexxa.core.convention;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;

import io.jexxa.application.applicationservice.InvalidApplicationService;
import io.jexxa.application.domainservice.IJexxaAggregateRepository;
import org.junit.jupiter.api.Test;

class PortConventionTest
{
    @Test
    void invalidPortConsturctor()
    {
        //Act/Assert
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationService.class));
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationServiceNoInterface.class));
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationServiceMultipleConstructor.class));
    }

    @SuppressWarnings("unused")
    public static class InvalidApplicationServiceMultipleConstructor
    {
        public InvalidApplicationServiceMultipleConstructor()
        {

        }

        public InvalidApplicationServiceMultipleConstructor(IJexxaAggregateRepository jexxaAggregateRepository)
        {
        }
    }

    @SuppressWarnings("unused")
    public static class InvalidApplicationServiceNoInterface
    {
        public InvalidApplicationServiceNoInterface(Properties properties)
        {

        }

    }

}
