package io.jexxa.core;

import io.jexxa.TestConstants;
import io.jexxa.testapplication.JexxaTestApplication;
import io.jexxa.testapplication.applicationservice.InvalidConstructorApplicationService;
import io.jexxa.testapplication.infrastructure.drivingadapter.generic.InvalidDrivingAdapter;
import io.jexxa.testapplication.infrastructure.drivingadapter.generic.ProxyDrivingAdapter;
import io.jexxa.core.convention.AdapterConventionViolation;
import io.jexxa.core.convention.PortConventionViolation;
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
        var drivingAdapter = jexxaMain.bind(ProxyDrivingAdapter.class);

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
        assertThrows(AdapterConventionViolation.class, () -> jexxaMain.bind(InvalidDrivingAdapter.class));
    }

}
