package io.jexxa.core.convention;

import io.jexxa.application.applicationservice.InvalidConstructorApplicationService;
import io.jexxa.application.domain.model.JexxaEntityRepository;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PortConventionTest
{
    @Test
    void invalidPortConstructor()
    {
        //Arrange - Nothing

        //Act/Assert
        assertThrows(PortConventionViolation.class, () -> PortConvention.validate(InvalidConstructorApplicationService.class)); // Violation: No public constructor
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
        public InvalidApplicationServiceMultipleConstructor(JexxaEntityRepository jexxaAggregateRepository)
        {
            Objects.requireNonNull(jexxaAggregateRepository);
        }
    }

    public static class InvalidApplicationServiceNoInterface
    {
        public InvalidApplicationServiceNoInterface(Properties properties)
        {
            Objects.requireNonNull(properties);
        }

    }

}
