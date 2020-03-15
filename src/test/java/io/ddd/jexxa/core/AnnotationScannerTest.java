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
        AnnotationScanner annotationScanner = new AnnotationScanner();

        List<Class<?>> applicationServiceList = annotationScanner.findClassAnnotation(ApplicationService.class);

        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }

    @Test
    public void findAnnotatedClassesWithinPackage() {
        String packageName = "io.ddd.jexxa.applicationservice";
        AnnotationScanner annotationScanner = new AnnotationScanner();

        List<Class<?>> applicationServiceList = annotationScanner.findClassAnnotation(ApplicationService.class, packageName);

        assertFalse(applicationServiceList.isEmpty());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }


    @Test
    public void findAnnotatedClassesFails() {
        var unavailableAnnotationAtRuntime = BusinessException.class;
        AnnotationScanner annotationScanner = new AnnotationScanner();

        List<Class<?>> applicationServiceList = annotationScanner.findClassAnnotation(unavailableAnnotationAtRuntime);

        assertTrue(applicationServiceList.isEmpty());
    }

    @Test
    public void findAnnotatedClassesFailsWithinPackage() {
        String invalidPackageName = "io.invalid.package";
        AnnotationScanner annotationScanner = new AnnotationScanner();

        List<Class<?>> applicationServiceList = annotationScanner.findClassAnnotation(ApplicationService.class, invalidPackageName);

        assertTrue(applicationServiceList.isEmpty());
    }

}
