package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
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


    @Test
    public void getAggregateByID()
    {
        //Arrange
        var objectUnderTest = JexxaAggregateRepository.create(new Properties());
        Supplier<Stream<Integer>> counterSupplier = () -> Stream.of(100);
        counterSupplier.get().
                map( counter -> JexxaAggregate.create(new JexxaValueObject(counter)) ).
                forEach( objectUnderTest::add );

        //Act
        var aggregateList = counterSupplier.get().
                map( key -> objectUnderTest.get(new JexxaValueObject(key))).
                collect( Collectors.toList() );

        //Assert
        Assert.assertEquals(counterSupplier.get().count(), aggregateList.size());
    }

}
