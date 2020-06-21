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
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationService.class)); // Violation: No public constructor
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationServiceNoInterface.class)); // Violation: Constructor does not take interfaces as argument
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationServiceMultipleConstructor.class)); // Violation: multiple constructor available
    }

    @SuppressWarnings("unused")
    public static class InvalidApplicationServiceMultipleConstructor
    {
        public InvalidApplicationServiceMultipleConstructor()
        {
            //Empty constructor for testing purpose
        }

        public InvalidApplicationServiceMultipleConstructor(IJexxaAggregateRepository jexxaAggregateRepository)
        {
            //Empty constructor for testing purpose
        }
    }

    @SuppressWarnings("unused")
    public static class InvalidApplicationServiceNoInterface
    {
        public InvalidApplicationServiceNoInterface(Properties properties)
        {
            //Empty constructor for testing purpose
        }

    }

}
