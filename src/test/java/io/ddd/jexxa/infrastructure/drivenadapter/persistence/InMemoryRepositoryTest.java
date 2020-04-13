package io.ddd.jexxa.infrastructure.drivenadapter.persistence;

import java.util.Properties;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import io.ddd.jexxa.infrastructure.drivenadapter.persistence.inmemory.InMemoryRepository;
import org.junit.Assert;
import org.junit.Test;

public class InMemoryRepositoryTest
{

    @Test
    public void addAggregate()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new InMemoryRepository<>(
                JexxaAggregate.class,
                JexxaValueObject.class,
                JexxaAggregate::getKey,
                new Properties()
        );

        //act
        objectUnderTest.add(aggregate);

        //Assert
        Assert.assertEquals(aggregate, objectUnderTest.get(aggregate.getKey()).orElse(null));
        Assert.assertTrue(objectUnderTest.get().size() > 0);
    }


    @Test
    public void removeAggregate()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new InMemoryRepository<>(
                JexxaAggregate.class,
                JexxaValueObject.class,
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.remove( aggregate.getKey() );

        //Assert
        Assert.assertTrue(objectUnderTest.get().isEmpty());
    }

}
