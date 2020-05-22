package io.jexxa.infrastructure.drivenadapter.persistence.jdbc;

import static io.jexxa.TestTags.UNIT_TEST;

import java.util.Properties;

import io.jexxa.application.domain.aggregate.JexxaAggregate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(UNIT_TEST)
class JDBCPropertiesTest
{
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
        propertiesInvalidURL.put(JDBCConnection.JDBC_URL, "jdbc:unknown://localhost:5432/jexxa");

        //3.Assert invalid properties: Invalid URL
        Assertions.assertThrows(IllegalArgumentException.class, () -> new JDBCConnection<>(
                JexxaAggregate.class,
                JexxaAggregate::getKey,
                propertiesInvalidURL
        ));
    }
}
