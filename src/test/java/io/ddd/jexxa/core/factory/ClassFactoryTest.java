package io.ddd.jexxa.core.factory;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;

import io.ddd.jexxa.core.factory.ClassFactory;
import io.ddd.jexxa.core.factory.DependencyScanner;
import io.ddd.stereotype.applicationcore.ApplicationService;
import org.junit.Test;

public class ClassFactoryTest
{
    @Test
    public void createApplicationService()
    {
        //Arrange
        var annotationScanner = new DependencyScanner();
        var factoryResults = new ArrayList<>();

        //Act
        var result = annotationScanner.getClassesWithAnnotation(ApplicationService.class);
        result.forEach( element -> factoryResults.add( ClassFactory.createByConstructor(element)) );

        //Assert
        assertFalse(factoryResults.isEmpty());

        result.forEach( element -> System.out.println(element.getSimpleName()));
    }
}
