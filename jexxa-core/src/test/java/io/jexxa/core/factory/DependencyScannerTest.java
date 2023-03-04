package io.jexxa.core.factory;

import io.jexxa.TestConstants;
import io.jexxa.adapterapi.drivingadapter.IDrivingAdapter;
import io.jexxa.testapplication.annotation.UnavailableDuringRuntime;
import io.jexxa.testapplication.annotation.ValidApplicationService;
import io.jexxa.testapplication.applicationservice.SimpleApplicationService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static io.jexxa.core.factory.PackageConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.core.factory.PackageConstants.JEXXA_DRIVING_ADAPTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class DependencyScannerTest
{
    @Test
    void findAnnotatedClassesWithinPackage() {
        //Arrange
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.
                acceptPackage(JEXXA_APPLICATION_SERVICE).
                getClassesWithAnnotation(ValidApplicationService.class);

        //Assert
        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }


    @Test
    void findAnnotatedClassesFailsWithinPackage() {
        //Arrange
        var invalidPackageName = "io.invalid.package";
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.
                acceptPackage(invalidPackageName).
                getClassesWithAnnotation(ValidApplicationService.class);

        //Assert
        assertTrue(applicationServiceList.isEmpty());
    }

    @Test
    void getClassesImplementingInterface() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        objectUnderTest.acceptPackage(JEXXA_DRIVING_ADAPTER);


        //Act
        List<Class<?>> drivingAdapters = objectUnderTest.getClassesImplementing(IDrivingAdapter.class);

        //Assert
        assertFalse(drivingAdapters.isEmpty());
    }


    @Test
    void getClassesImplementingInterfaceInSpecificPackage() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        var packageName = "io.jexxa.drivingadapter.messaging";

        //Act
        List<Class<?>> drivingAdapters = objectUnderTest.
                acceptPackage(packageName).
                getClassesImplementing(IDrivingAdapter.class);

        //Assert
        assertFalse(drivingAdapters.isEmpty());
        assertEquals(1, drivingAdapters.size());
    }

    @Test
    void handleAnnotationUnavailableDuringRuntime()
    {
        //Arrange
        var objectUnderTest = new DependencyScanner();

        //Act
        assertThrows(IllegalArgumentException.class, () -> objectUnderTest.getClassesWithAnnotation(UnavailableDuringRuntime.class));
    }

}
