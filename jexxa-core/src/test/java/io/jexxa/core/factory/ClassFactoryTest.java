package io.jexxa.core.factory;


import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import io.jexxa.TestConstants;
import io.jexxa.application.annotation.ApplicationService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class ClassFactoryTest
{
    @Test
    protected void createApplicationService()
    {
        //Arrange
        var annotationScanner = new DependencyScanner();
        annotationScanner.whiteListPackage(JEXXA_APPLICATION_SERVICE);

        var factoryResults = new ArrayList<>();

        //Act
        var result = annotationScanner.getClassesWithAnnotation(ApplicationService.class);
        result.forEach( element -> factoryResults.add( ClassFactory.newInstanceOf(element)) );

        //Assert
        assertFalse(factoryResults.isEmpty());
    }
}
