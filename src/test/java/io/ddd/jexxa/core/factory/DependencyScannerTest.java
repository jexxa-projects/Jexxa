package io.ddd.jexxa.core.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import io.ddd.jexxa.applicationcore.applicationservice.SimpleApplicationService;
import io.ddd.jexxa.core.annotation.ApplicationService;
import io.ddd.jexxa.core.annotation.UnavailableDuringRuntime;
import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import org.junit.Test;

public class DependencyScannerTest
{
    @Test
    public void findAnnotatedClasses() {
        //Arrange
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.getClassesWithAnnotation(ApplicationService.class);

        //Assert
        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }

    @Test
    public void findAnnotatedClassesWithinPackage() {
        //Arrange
        var packageName = "io.ddd.jexxa.applicationcore.applicationservice";
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
    public void findAnnotatedClassesFailsWithinPackage() {
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
    public void getClassesImplementingInterface() {
        //Arrange
        var objectUnderTest = new DependencyScanner();

        //Act
        List<Class<?>> drivingAdapters = objectUnderTest.getClassesImplementing(IDrivingAdapter.class);

        //Assert
        assertFalse(drivingAdapters.isEmpty());
    }


    @Test
    public void getClassesInPackageImplementingInterface() {
        //Arrange
        var objectUnderTest = new DependencyScanner();
        var packageName = "io.ddd.jexxa.infrastructure.drivingadapter.rest";

        //Act
        List<Class<?>> drivingAdapters = objectUnderTest.
                whiteListPackage(packageName).
                getClassesImplementing(IDrivingAdapter.class);

        //Assert
        assertFalse(drivingAdapters.isEmpty());
        assertEquals(1, drivingAdapters.size());
    }

    @Test (expected = IllegalArgumentException.class)
    public void handleAnnotationUnavailableDuringRuntime()
    {
        //Arrange
        var objectUnderTest = new DependencyScanner();

        //Act
        objectUnderTest.getClassesWithAnnotation(UnavailableDuringRuntime.class);
    }

}
