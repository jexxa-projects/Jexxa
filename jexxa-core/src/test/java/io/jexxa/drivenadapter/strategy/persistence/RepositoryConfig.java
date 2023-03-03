package io.jexxa.drivenadapter.strategy.persistence;

import io.jexxa.api.wrapper.jdbc.JexxaJDBCProperties;

import java.util.Properties;
import java.util.stream.Stream;

public class RepositoryConfig {
    static private final String USER_NAME = "postgres";
    static private final String USER_PASSWORD = "admin";
    @SuppressWarnings("unused")
    public static Stream<Properties> repositoryConfig(String schemaName) {
        return Stream.of(
                postgresRepositoryConfigSerializable(schemaName),
                postgresRepositoryConfigRepeatableRead(schemaName),
                postgresRepositoryConfig(schemaName),
                h2RepositoryConfig(),
                imdbRepositoryConfig()
        );
    }

    public static Stream<Properties> jdbcRepositoryConfig(String schemaName) {
        return Stream.of(
                postgresRepositoryConfig(schemaName),
                h2RepositoryConfig()
        );
    }

    public static Properties postgresRepositoryConfig(String schemaName) {
        var postgresProperties = new Properties();
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_PASSWORD, USER_PASSWORD);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_USERNAME, USER_NAME);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_URL, "jdbc:postgresql://localhost:5432/" + schemaName);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");
        return  postgresProperties;
    }

    public static Properties postgresRepositoryConfigSerializable(String schemaName) {
        var postgresProperties = new Properties();
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_PASSWORD, USER_PASSWORD);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_USERNAME, USER_NAME);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_URL, "jdbc:postgresql://localhost:5432/" + schemaName);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_TRANSACTION_ISOLATION_LEVEL, "serializable");
        return  postgresProperties;
    }

    public static Properties postgresRepositoryConfigRepeatableRead(String schemaName) {
        var postgresProperties = new Properties();
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_PASSWORD, USER_PASSWORD);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_USERNAME, USER_NAME);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_URL, "jdbc:postgresql://localhost:5432/" + schemaName);
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");
        postgresProperties.put(JexxaJDBCProperties.JEXXA_JDBC_TRANSACTION_ISOLATION_LEVEL, "repeatable-read");
        return  postgresProperties;
    }


    public static Properties h2RepositoryConfig() {
        var h2Properties = new Properties();
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_PASSWORD, USER_PASSWORD);
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_USERNAME, USER_NAME);
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_URL, "jdbc:h2:mem:jexxa;DB_CLOSE_DELAY=-1");
        h2Properties.put(JexxaJDBCProperties.JEXXA_JDBC_AUTOCREATE_TABLE, "true");

        return h2Properties;
    }


    public static Properties imdbRepositoryConfig()
    {
        return new Properties();
    }

}
