package io.ddd.jexxa.infrastructure.drivenadapter.persistence.jdbc;

import java.util.Properties;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.Assert;
import org.junit.Test;

public class JDBCConnectionTest
{

    @Test
    public void addAggregate()
    {
        //Arrange
        var aggregate = JexxaAggregate.create(new JexxaValueObject(42));
        var properties = new Properties();
        properties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        properties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        properties.put(JDBCConnection.JDBC_USERNAME, "admin");
        properties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        properties.put(JDBCConnection.JDBC_AUTOCREATE, "true");


        var objectUnderTest = new JDBCConnection<>(JexxaAggregate.class, JexxaValueObject.class, JexxaAggregate::getKey, properties);

        System.out.println("HERE");
        objectUnderTest.removeAll();

        //act
        objectUnderTest.add(aggregate);

        //Assert
        //Assert.assertEquals(aggregate.getKey(), objectUnderTest.get(aggregate.getKey()).orElseThrow().getKey());
        //Assert.assertTrue(objectUnderTest.get().size() > 0);
    }
}