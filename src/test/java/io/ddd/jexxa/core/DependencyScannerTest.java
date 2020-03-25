package io.ddd.jexxa.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;

import java.util.List;

import io.ddd.jexxa.infrastructure.drivingadapter.IDrivingAdapter;
import io.ddd.stereotype.applicationcore.ApplicationService;
import io.ddd.stereotype.applicationcore.BusinessException;
import org.junit.Test;

public class DependencyScannerTest
{
    @Test
    public void findAnnotatedClasses() {
        //Arrange
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.getClassAnnotation(ApplicationService.class);

        //Assert
        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }

    @Test
    public void findAnnotatedClassesWithinPackage() {
        //Arrange
        var packageName = "io.ddd.jexxa.applicationservice";
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.getClassAnnotation(ApplicationService.class, packageName);

        //Assert
        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }


    @Test
    public void findAnnotatedClassesFails() {
        //Arrange
        var unavailableAnnotationAtRuntime = BusinessException.class;
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.getClassAnnotation(unavailableAnnotationAtRuntime);

        //Assert
        assertTrue(applicationServiceList.isEmpty());
    }

    @Test
    public void findAnnotatedClassesFailsWithinPackage() {
        //Arrange
        var invalidPackageName = "io.invalid.package";
        var objectUnderTest = new DependencyScanner();

        //Act
        var applicationServiceList = objectUnderTest.getClassAnnotation(ApplicationService.class, invalidPackageName);

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
        List<Class<?>> drivingAdapters = objectUnderTest.getClassesImplementing(IDrivingAdapter.class, packageName);

        //Assert
        assertFalse(drivingAdapters.isEmpty());
        assertEquals(1, drivingAdapters.size());
    }

}
