package io.ddd.jexxa.infrastructure.drivenadapter.persistence.jdbc;

import java.util.Properties;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JDBCConnectionTest
{
    private JexxaAggregate aggregate;
    private JDBCConnection<JexxaAggregate,JexxaValueObject> objectUnderTest;

    @BeforeEach
    public void initTests()
    {
        //Arrange
        aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var properties = new Properties();
        properties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        properties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        properties.put(JDBCConnection.JDBC_USERNAME, "admin");
        properties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        properties.put(JDBCConnection.JDBC_DEFAULT_URL, "jdbc:postgresql://localhost:5432/postgres");
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
        Assertions.assertEquals(aggregate.getKey(), objectUnderTest.get(aggregate.getKey()).orElseThrow().getKey());
        Assertions.assertTrue(objectUnderTest.get().size() > 0);
    }

    @Test
    public void addAggregateTwice()
    {
        //act
        objectUnderTest.add(aggregate);
        Assertions.assertThrows(IllegalArgumentException.class, () -> objectUnderTest.add(aggregate));
    }

    @Test
    public void removeAggregate()
    {
        //Arrange
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.remove( aggregate.getKey() );

        //Assert
        Assertions.assertTrue(objectUnderTest.get().isEmpty());
    }

}