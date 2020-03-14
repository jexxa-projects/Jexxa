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
        AnnotationScanner annotationScanner = new AnnotationScanner("io.ddd.jexxa.applicationservice");
        findApplicationService(annotationScanner);
    }

    @Test
    public void findApplicationServiceWithoutPacakgeName() {
        AnnotationScanner annotationScanner = new AnnotationScanner();
        findApplicationService(annotationScanner);
    }

    public void findApplicationService(AnnotationScanner annotationScanner) {
        List<Class<?>> applicationServiceList = annotationScanner.findAnnotation(ApplicationService.class);

        assertFalse(applicationServiceList.isEmpty());
        assertEquals(1, applicationServiceList.size());
        assertTrue(applicationServiceList
                .stream()
                .anyMatch(SimpleApplicationService.class::isAssignableFrom));
    }
}
