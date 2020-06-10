package io.jexxa.core.factory;


import static io.jexxa.TestConstants.JEXXA_APPLICATION_SERVICE;
import static io.jexxa.utils.ThrowingConsumer.exceptionCollector;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void createApplicationService()
    {
        //Arrange
        var annotationScanner = new DependencyScanner();
        annotationScanner.whiteListPackage(JEXXA_APPLICATION_SERVICE);
        var collectedException = new ArrayList<Throwable>();

        var factoryResults = new ArrayList<>();

        //Act
        var result = annotationScanner.getClassesWithAnnotation(ApplicationService.class);
        result.forEach( exceptionCollector(element -> factoryResults.add( ClassFactory.newInstanceOf(element)), collectedException));

        //Assert
        assertTrue(collectedException.isEmpty());
        assertFalse(factoryResults.isEmpty());
    }
}
