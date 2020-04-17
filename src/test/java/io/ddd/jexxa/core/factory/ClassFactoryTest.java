package io.ddd.jexxa.core.factory;


import java.util.ArrayList;

import io.ddd.jexxa.application.annotation.ApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        result.forEach( element -> factoryResults.add( ClassFactory.newInstanceOf(element)) );

        //Assert
        Assertions.assertFalse(factoryResults.isEmpty());

        result.forEach( element -> System.out.println(element.getSimpleName()));
    }
}
