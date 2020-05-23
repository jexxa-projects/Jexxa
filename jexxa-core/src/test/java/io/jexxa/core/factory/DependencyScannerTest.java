package io.jexxa.core.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import io.jexxa.TestConstants;
import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.annotation.UnavailableDuringRuntime;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class DependencyScannerTest
{
    @Test
    protected void findAnnotatedClasses() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        var packageName = "io.jexxa.application";
        objectUnderTest.whiteListPackage(packageName);

        //Act
        var applicationServiceList = objectUnderTest.getClassesWithAnnotation(ApplicationService.class);

        //Assert
        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }

    @Test
    protected void findAnnotatedClassesWithinPackage() {
        //Arrange
        var packageName = "io.jexxa.application";
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.
                whiteListPackage(packageName).
                getClassesWithAnnotation(ApplicationService.class);

        //Assert
        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }
    

    @Test
    protected void findAnnotatedClassesFailsWithinPackage() {
        //Arrange
        var invalidPackageName = "io.invalid.package";
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.
                whiteListPackage(invalidPackageName).
                getClassesWithAnnotation(ApplicationService.class);

        //Assert
        assertTrue(applicationServiceList.isEmpty());
    }

    @Test
    protected void getClassesImplementingInterface() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        var packageName = "io.jexxa.infrastructure";
        objectUnderTest.whiteListPackage(packageName);


        //Act
        List<Class<?>> drivingAdapters = objectUnderTest.getClassesImplementing(IDrivingAdapter.class);

        //Assert
        assertFalse(drivingAdapters.isEmpty());
    }


    @Test
    protected void getClassesInPackageImplementingInterface() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        var packageName = "io.jexxa.infrastructure.drivingadapter.rest";

        //Act
        List<Class<?>> drivingAdapters = objectUnderTest.
                whiteListPackage(packageName).
                getClassesImplementing(IDrivingAdapter.class);

        //Assert
        assertFalse(drivingAdapters.isEmpty());
        assertEquals(1, drivingAdapters.size());
    }

    @Test 
    protected void handleAnnotationUnavailableDuringRuntime()
    {
        //Arrange
        var objectUnderTest = new DependencyScanner();

        //Act
        assertThrows(IllegalArgumentException.class, () -> objectUnderTest.getClassesWithAnnotation(UnavailableDuringRuntime.class));
    }

}
