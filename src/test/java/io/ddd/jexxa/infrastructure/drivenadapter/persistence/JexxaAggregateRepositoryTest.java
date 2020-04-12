package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import io.ddd.jexxa.application.infrastructure.drivenadapter.persistence.JexxaAggregateRepository;
import org.junit.Assert;
import org.junit.Test;

public class JexxaAggregateRepositoryTest
{
    @Test
    public void addAggregate()
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(new Properties());
        var counterStream = Stream.of(100);
        var aggregateList = counterStream.
                map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) ).
                collect( Collectors.toList() );

        //Act
        aggregateList.forEach(objectUnderTest::add);

        //Assert
        Assert.assertEquals(aggregateList.size(), objectUnderTest.get().size());
    }
}
