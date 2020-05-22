package io.jexxa.core;

import static io.jexxa.TestTags.UNIT_TEST;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jexxa.application.applicationservice.InvalidApplicationService;
import io.jexxa.application.infrastructure.drivingadapter.InvalidAdapter;
import io.jexxa.core.convention.AdapterConventionViolation;
import io.jexxa.core.convention.PortConventionViolation;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(UNIT_TEST)
public class DrivingAdapterTest
{

    @Test
    void throwOnInvalidPortConvention()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(DrivingAdapterTest.class.getSimpleName());

        //Act / Assert
        assertThrows(PortConventionViolation.class, () -> jexxaMain.bind(JMXAdapter.class).to(InvalidApplicationService.class));
    }

    @Test
    void throwOnInvalidAdapterConvention()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(DrivingAdapterTest.class.getSimpleName());

        //Act / Assert
        assertThrows(AdapterConventionViolation.class, () -> jexxaMain.bind(InvalidAdapter.class));
    }

}
