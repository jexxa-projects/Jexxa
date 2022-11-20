package io.jexxa.infrastructure.drivenadapterstrategy.persistence;

import io.jexxa.utils.properties.JexxaJDBCProperties;

import java.util.Properties;
import java.util.stream.Stream;

public class RepositoryConfig {
    static private final String USER_NAME = "postgres";
    static private final String USER_PASSWORD = "admin";
    @SuppressWarnings("unused")
    public static Stream<Properties> repositoryConfig(String schemaName) {
        var postgresProperties = new Properties();
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_PASSWORD, USER_PASSWORD);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_USERNAME, USER_NAME);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_URL, "jdbc:postgresql://localhost:5432/" + schemaName);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        var h2Properties = new Properties();
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_PASSWORD, USER_PASSWORD);
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_USERNAME, USER_NAME);
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_URL, "jdbc:h2:mem:jexxa;DB_CLOSE_DELAY=-1");
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_TABLE, "true");

        return Stream.of(postgresProperties, h2Properties);
    }
}
