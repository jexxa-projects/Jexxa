package io.ddd.jexxa.infrastructure.drivenadapter.persistence.jdbc;

import java.util.Properties;
import java.util.function.Function;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import io.ddd.jexxa.infrastructure.drivenadapter.persistence.imdb.IMDBConnection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JDBCConnectionTest
{
    private JexxaAggregate aggregate;
    private JDBCConnection<JexxaAggregate,JexxaValueObject> objectUnderTest;

    @Before
    public void initTests()
    {
        //Arrange
        aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var properties = new Properties();
        properties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        properties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        properties.put(JDBCConnection.JDBC_USERNAME, "admin");
        properties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        properties.put(JDBCConnection.JDBC_AUTOCREATE, "true");

        objectUnderTest = new JDBCConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                properties
        );
        objectUnderTest.removeAll();
    }

    @Test
    public void addAggregate()
    {
        //act
        objectUnderTest.add(aggregate);

        //Assert
        Assert.assertEquals(aggregate.getKey(), objectUnderTest.get(aggregate.getKey()).orElseThrow().getKey());
        Assert.assertTrue(objectUnderTest.get().size() > 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addAggregateTwice()
    {
        //act
        objectUnderTest.add(aggregate);
        objectUnderTest.add(aggregate);
    }

    @Test
    public void removeAggregate()
    {
        //Arrange
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.remove( aggregate.getKey() );

        //Assert
        Assert.assertTrue(objectUnderTest.get().isEmpty());
    }

}