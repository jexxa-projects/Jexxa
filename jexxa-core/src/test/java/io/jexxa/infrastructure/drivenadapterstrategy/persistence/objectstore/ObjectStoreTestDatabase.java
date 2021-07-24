package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore;

import java.util.Properties;
import java.util.stream.Stream;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;

public class ObjectStoreTestDatabase
{
    public static final String REPOSITORY_CONFIG = "io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.ObjectStoreTestDatabase#repositoryConfig";

    @SuppressWarnings("unused")
    public static Stream<Properties> repositoryConfig() {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCConnection.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/multiindexrepository");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        var h2Properties = new Properties();
        h2Properties.put(JDBCConnection.JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        h2Properties.put(JDBCConnection.JDBC_USERNAME, "admin");
        h2Properties.put(JDBCConnection.JDBC_URL, "jdbc:h2:mem:ComparableRepositoryTest;DB_CLOSE_DELAY=-1");
        h2Properties.put(JDBCConnection.JDBC_AUTOCREATE_TABLE, "true");

        return Stream.of(new Properties(), postgresProperties, h2Properties);
    }

}
