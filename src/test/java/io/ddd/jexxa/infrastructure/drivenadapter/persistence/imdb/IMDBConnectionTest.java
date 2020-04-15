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
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();

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
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();
        
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
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.remove( aggregate.getKey() );

        //Assert
        Assert.assertTrue(objectUnderTest.get().isEmpty());
    }

    @Test
    public void differentConnections()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();
        objectUnderTest.add(aggregate);

        //act
        var newConnection = new IMDBConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );

        //Assert that connections are different but refer to the same repository 
        Assert.assertNotEquals(objectUnderTest, newConnection);
        Assert.assertFalse(objectUnderTest.get().isEmpty());
        Assert.assertFalse(newConnection.get().isEmpty());
    }

    @Test
    public void differentRepositories()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var objectUnderTest = new IMDBConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        );
        objectUnderTest.removeAll();
        objectUnderTest.add(aggregate);

        //act
        var newConnection = new IMDBConnection<>(
                JexxaValueObject.class,
                JexxaValueObject::getValue,
                new Properties()
        );
        newConnection.removeAll();
        newConnection.add(new JexxaValueObject(42));

        //Assert that connections are different but refer to the same repository
        Assert.assertNotEquals(objectUnderTest, newConnection);
        Assert.assertEquals(1, objectUnderTest.get().size());
        Assert.assertEquals(1, newConnection.get().size());

    }


}
