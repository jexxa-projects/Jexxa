package io.jexxa.core.convention;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;

import io.jexxa.application.applicationservice.InvalidApplicationService;
import io.jexxa.application.domainservice.IJexxaAggregateRepository;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.Test;

class PortConventionTest
{
    @Test
    void invalidPortConstructor()
    {
        //Act/Assert
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationService.class)); // Violation: No public constructor
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationServiceNoInterface.class)); // Violation: Constructor does not take interfaces as argument
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidApplicationServiceMultipleConstructor.class)); // Violation: multiple constructor available
    }

    public static class InvalidApplicationServiceMultipleConstructor
    {
        @SuppressWarnings("unused")
        public InvalidApplicationServiceMultipleConstructor()
        {
            //Empty constructor for testing purpose
        }

        @SuppressWarnings("unused")
        public InvalidApplicationServiceMultipleConstructor(IJexxaAggregateRepository jexxaAggregateRepository)
        {
            Validate.notNull(jexxaAggregateRepository);
        }
    }

    public static class InvalidApplicationServiceNoInterface
    {
        public InvalidApplicationServiceNoInterface(Properties properties)
        {
            Validate.notNull(properties);
        }

    }

}
