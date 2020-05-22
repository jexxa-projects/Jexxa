package io.jexxa.core.factory;


import static io.jexxa.TestTags.UNIT_TEST;

import java.util.ArrayList;

import io.jexxa.application.annotation.ApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(UNIT_TEST)
public class ClassFactoryTest
{
    @Test
    void createApplicationService()
    {
        //Arrange
        var annotationScanner = new DependencyScanner();
        annotationScanner.whiteListPackage("io.jexxa.application.applicationservice");

        var factoryResults = new ArrayList<>();

        //Act
        var result = annotationScanner.getClassesWithAnnotation(ApplicationService.class);
        result.forEach( element -> factoryResults.add( ClassFactory.newInstanceOf(element)) );

        //Assert
        Assertions.assertFalse(factoryResults.isEmpty());
    }
}
