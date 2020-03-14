package io.ddd.jexxa.infrastructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import io.ddd.jexxa.applicationservice.SimpleApplicationService;

import java.util.List;

import io.ddd.stereotype.applicationcore.ApplicationService;
import org.junit.Test;

public class AnnotationScannerTest
{
    @Test
    public void findApplicationServiceWithPacakgeName() {
        String packageName = "io.ddd.jexxa.applicationservice";
        AnnotationScanner annotationScanner = new AnnotationScanner();

        List<Class<?>> applicationServiceList = annotationScanner.findClassAnnotation(ApplicationService.class, packageName);

        assertFalse(applicationServiceList.isEmpty());
        assertEquals(1, applicationServiceList.size());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }


    @Test
    public void findApplicationServiceWithInvalidPacakgeName() {
        String invalidPackageName = "io.invalid.package";
        AnnotationScanner annotationScanner = new AnnotationScanner();

        List<Class<?>> applicationServiceList = annotationScanner.findClassAnnotation(ApplicationService.class, invalidPackageName);

        assertTrue(applicationServiceList.isEmpty());
    }

    @Test
    public void findApplicationServiceWithoutPacakgeName() {
        AnnotationScanner annotationScanner = new AnnotationScanner();

        List<Class<?>> applicationServiceList = annotationScanner.findClassAnnotation(ApplicationService.class);

        assertFalse(applicationServiceList.isEmpty());
        assertEquals(1, applicationServiceList.size());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));

    }
}
