package io.jexxa.utils.factory;


import io.jexxa.TestConstants;
import io.jexxa.api.wrapper.factory.ClassFactory;
import io.jexxa.application.applicationservice.Java8DateTimeApplicationService;
import io.jexxa.application.applicationservice.SimpleApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.ArrayList;
import java.util.List;

import static io.jexxa.utils.function.ThrowingConsumer.exceptionCollector;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
@Tag(TestConstants.UNIT_TEST)
class ClassFactoryTest
{
    @Test
    void createApplicationServices()
    {
        //Arrange
        var collectedException = new ArrayList<Throwable>();

        var factoryResults = new ArrayList<>();

        var validApplicationServices = List.of(SimpleApplicationService.class, Java8DateTimeApplicationService.class, SimpleApplicationService.class);
        //Act
        validApplicationServices.forEach( exceptionCollector(element -> factoryResults.add( ClassFactory.newInstanceOf(element)), collectedException));

        //Assert
        assertTrue(collectedException.isEmpty());
        assertFalse(factoryResults.isEmpty());
        factoryResults.forEach(Assertions::assertNotNull);
    }
}
