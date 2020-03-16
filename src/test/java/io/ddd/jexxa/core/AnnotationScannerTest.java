package io.ddd.jexxa.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;

import java.util.List;

import io.ddd.jexxa.core.AnnotationScanner;
import io.ddd.stereotype.applicationcore.ApplicationService;
import io.ddd.stereotype.applicationcore.BusinessException;
import org.junit.Test;

public class AnnotationScannerTest
{
    @Test
    public void findAnnotatedClasses() {
        //Arrange
        var objectUnderTest = new AnnotationScanner();

        //Act
        var applicationServiceList = objectUnderTest.findClassAnnotation(ApplicationService.class);

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
        var objectUnderTest = new AnnotationScanner();

        //Act
        var applicationServiceList = objectUnderTest.findClassAnnotation(ApplicationService.class, packageName);

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
        var objectUnderTest = new AnnotationScanner();

        //Act
        var applicationServiceList = objectUnderTest.findClassAnnotation(unavailableAnnotationAtRuntime);

        //Assert
        assertTrue(applicationServiceList.isEmpty());
    }

    @Test
    public void findAnnotatedClassesFailsWithinPackage() {
        //Arrange
        var invalidPackageName = "io.invalid.package";
        var objectUnderTest = new AnnotationScanner();

        //Act
        var applicationServiceList = objectUnderTest.findClassAnnotation(ApplicationService.class, invalidPackageName);

        //Assert
        assertTrue(applicationServiceList.isEmpty());
    }

}
