package io.jexxa.core;

import io.jexxa.TestConstants;
import io.jexxa.application.JexxaTestApplication;
import io.jexxa.application.applicationservice.InvalidConstructorApplicationService;
import io.jexxa.application.infrastructure.drivingadapter.InvalidAdapter;
import io.jexxa.core.convention.AdapterConventionViolation;
import io.jexxa.core.convention.PortConventionViolation;
import io.jexxa.infrastructure.drivingadapter.jmx.JMXAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag(TestConstants.UNIT_TEST)
class DrivingAdapterTest
{

    @Test
    void throwOnInvalidPortConvention()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(JexxaTestApplication.class);
        var drivingAdapter = jexxaMain.bind(JMXAdapter.class);

        //Act / Assert
        assertThrows(PortConventionViolation.class, () -> drivingAdapter.to(InvalidConstructorApplicationService.class));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void throwOnInvalidAdapterConvention()
    {
        //Arrange
        JexxaMain jexxaMain = new JexxaMain(DrivingAdapterTest.class);

        //Act / Assert
        assertThrows(AdapterConventionViolation.class, () -> jexxaMain.bind(InvalidAdapter.class));
    }

}
