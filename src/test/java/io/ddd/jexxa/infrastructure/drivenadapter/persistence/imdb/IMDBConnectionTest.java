package io.ddd.jexxa.infrastructure.drivenadapter.persistence.imdb;

import java.util.Properties;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.Assert;
import org.junit.Test;

public class IMDBConnectionTest
{

    @Test
    public void addAggregate()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBConnection<>(
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

    @Test(expected = IllegalArgumentException.class)
    public void addAggregateTwice()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBConnection<>(
                JexxaAggregate.class,
                JexxaValueObject.class,
                JexxaAggregate::getKey,
                new Properties()
        );

        //act
        objectUnderTest.add(aggregate);
        objectUnderTest.add(aggregate);
    }


    @Test
    public void removeAggregate()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBConnection<>(
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
