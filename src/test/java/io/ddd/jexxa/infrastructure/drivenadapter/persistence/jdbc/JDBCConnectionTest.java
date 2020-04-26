package io.ddd.jexxa.infrastructure.drivenadapter.persistence.jdbc;

import java.util.Properties;

import io.ddd.jexxa.application.domain.aggregate.JexxaAggregate;
import io.ddd.jexxa.application.domain.valueobject.JexxaValueObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("integration-test")
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

    @AfterEach
    public void teardown()
    {
        if ( objectUnderTest != null )
        {
            objectUnderTest.close();
        }
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
    public void removeAggregate()
    {
        //Arrange
        objectUnderTest.add(aggregate);

        //act
        objectUnderTest.remove( aggregate.getKey() );

        //Assert
        Assertions.assertTrue(objectUnderTest.get().isEmpty());
    }


    @Test
    public void testExceptionInvalidOperations()
    {
        //Exception if key is used to add twice  
        objectUnderTest.add(aggregate);
        Assertions.assertThrows(IllegalArgumentException.class, () -> objectUnderTest.add(aggregate));

        //Exception if illegal key is removed
        objectUnderTest.remove(aggregate.getKey());
        Assertions.assertThrows(IllegalArgumentException.class, () -> objectUnderTest.remove(aggregate.getKey()));

        //Exception if unknown aggregate ist updated
        Assertions.assertThrows(IllegalArgumentException.class, () ->objectUnderTest.update(aggregate));
    }

    @Test
    public void invalidProperties()
    {
        //1.Assert missing properties
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JDBCConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                new Properties()
        ));

        //2.Arrange invalid properties: Invalid Driver
        Properties propertiesInvalidDriver = new Properties();
        propertiesInvalidDriver.put(JDBCConnection.JDBC_DRIVER, "org.unknown.Driver");
        propertiesInvalidDriver.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");

        //2.Assert invalid properties: Invalid Driver 
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JDBCConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                propertiesInvalidDriver
        ));

        //3. Arrange invalid properties: Invalid URL
        Properties propertiesInvalidURL = new Properties();
        propertiesInvalidURL.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        propertiesInvalidURL.put(JDBCConnection.JDBC_URL, "jdbc:unknonwn://localhost:5432/jexxa");

        //3.Assert invalid properties: Invalid URL
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JDBCConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                propertiesInvalidURL
        ));
    }

}