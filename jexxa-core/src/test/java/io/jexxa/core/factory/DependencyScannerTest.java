package io.jexxa.core.factory;

import static io.jexxa.TestTags.UNIT_TEST;

import java.util.List;

import io.jexxa.TestTags;
import io.jexxa.application.annotation.ApplicationService;
import io.jexxa.application.annotation.UnavailableDuringRuntime;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import io.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestTags.UNIT_TEST)
class DependencyScannerTest
{
    @Test
    void findAnnotatedClasses() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        var packageName = "io.jexxa.application";
        objectUnderTest.whiteListPackage(packageName);

        //Act
        var applicationServiceList = objectUnderTest.getClassesWithAnnotation(ApplicationService.class);

        //Assert
        Assertions.assertFalse(applicationServiceList.isEmpty());
        Assertions.assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }

    @Test
    void findAnnotatedClassesWithinPackage() {
        //Arrange
        var packageName = "io.jexxa.application";
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.
                whiteListPackage(packageName).
                getClassesWithAnnotation(ApplicationService.class);

        //Assert
        Assertions.assertFalse(applicationServiceList.isEmpty());
        Assertions.assertTrue(applicationServiceList
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
                whiteListPackage(invalidPackageName).
                getClassesWithAnnotation(ApplicationService.class);

        //Assert
        Assertions.assertTrue(applicationServiceList.isEmpty());
    }

    @Test
    void getClassesImplementingInterface() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        var packageName = "io.jexxa.infrastructure";
        objectUnderTest.whiteListPackage(packageName);


        //Act
        List<Class<?>> drivingAdapters = objectUnderTest.getClassesImplementing(IDrivingAdapter.class);

        //Assert
        Assertions.assertFalse(drivingAdapters.isEmpty());
    }


    @Test
    public void getClassesInPackageImplementingInterface() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        var packageName = "io.jexxa.infrastructure.drivingadapter.rest";

        //Act
        List<Class<?>> drivingAdapters = objectUnderTest.
                whiteListPackage(packageName).
                getClassesImplementing(IDrivingAdapter.class);

        //Assert
        Assertions.assertFalse(drivingAdapters.isEmpty());
        Assertions.assertEquals(1, drivingAdapters.size());
    }

    @Test 
    void handleAnnotationUnavailableDuringRuntime()
    {
        //Arrange
        var objectUnderTest = new DependencyScanner();

        //Act
        Assertions.assertThrows(IllegalArgumentException.class, () -> objectUnderTest.getClassesWithAnnotation(UnavailableDuringRuntime.class));
    }

}
