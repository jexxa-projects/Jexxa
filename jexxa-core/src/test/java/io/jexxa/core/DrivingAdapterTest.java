package io.jexxa.core;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.jexxa.TestConstants;
import io.jexxa.application.applicationservice.InvalidApplicationService;
import io.jexxa.application.infrastructure.drivingadapter.InvalidAdapter;
import io.jexxa.core.convention.AdapterConventionViolation;
import io.jexxa.core.convention.PortConventionViolation;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestConstants.UNIT_TEST)
class DrivingAdapterTest
{

    @Test
    protected void throwOnInvalidPortConvention()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(DrivingAdapterTest.class.getSimpleName());
        var drivingAdapter = jexxaMain.bind(JMXAdapter.class);

        //Act / Assert
        assertThrows(PortConventionViolation.class, () -> drivingAdapter.to(InvalidApplicationService.class));
    }

    @Test
    protected void throwOnInvalidAdapterConvention()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(DrivingAdapterTest.class.getSimpleName());

        //Act / Assert
        assertThrows(AdapterConventionViolation.class, () -> jexxaMain.bind(InvalidAdapter.class));
    }

}
