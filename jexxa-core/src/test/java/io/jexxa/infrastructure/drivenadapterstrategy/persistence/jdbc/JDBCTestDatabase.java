package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCTableBuilder.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.DOUBLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.FLOAT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.INTEGER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.NUMERIC;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.TEXT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.TIMESTAMP;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.stream.Stream;

public class JDBCTestDatabase
{
    static final int PRIMARY_KEY_WITH_NULL_VALUES = 1;
    static final int PRIMARY_KEY_WITH_NONNULL_VALUES = 2;
    static final int PRIMARY_KEY_WITH_NONNULL_VALUES_DUPLICATE = 3;
    static final int PRIMARY_KEY_VALUES_NOT_PRESENT = 4;
    static final Timestamp TEST_TIMESTAMP = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MICROS));
    static final String TEST_STRING = "Hello World";
    static final int TEST_INT_VALUE = 2;
    static final int TEST_FLOAT_VALUE = 3;
    static final int TEST_DOUBLE_VALUE = 4;
    static final BigDecimal TEST_NUMERIC_VALUE = BigDecimal.valueOf(5);


    enum JDBCTestSchema
    {
        KEY,
        INTEGER_TYPE,
        NUMERIC_TYPE,
        FLOAT_TYPE,
        DOUBLE_TYPE,
        STRING_TYPE,
        TIMESTAMP_TYPE,
    }

    public static final String REPOSITORY_CONFIG = "io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase#repositoryConfig";

    @SuppressWarnings("unused")
    static Stream<Properties> repositoryConfig() {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCConnection.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        var h2Properties = new Properties();
        h2Properties.put(JDBCConnection.JDBC_DRIVER, "org.h2.Driver");
        h2Properties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        h2Properties.put(JDBCConnection.JDBC_USERNAME, "admin");
        h2Properties.put(JDBCConnection.JDBC_URL, "jdbc:h2:mem:jexxa;DB_CLOSE_DELAY=-1");
        h2Properties.put(JDBCConnection.JDBC_AUTOCREATE_TABLE, "true");

        return Stream.of(postgresProperties, h2Properties);
    }


    static JDBCConnection setupDatabase(Properties properties)
    {
        var jdbcConnection = new JDBCConnection(properties);
        dropTable(jdbcConnection);
        autocreateTable(jdbcConnection);
        insertTestData(jdbcConnection);

        return jdbcConnection;
    }


    static void autocreateTable(JDBCConnection jdbcConnection)
    {
        var createTableCommand = jdbcConnection.createTableCommand(JDBCTestSchema.class)
                .createTableIfNotExists(JDBCTestDatabase.class)
                .addColumn(KEY, INTEGER)
                .addConstraint(PRIMARY_KEY)
                .addColumn(INTEGER_TYPE, INTEGER)
                .addColumn(NUMERIC_TYPE, NUMERIC)
                .addColumn(FLOAT_TYPE, FLOAT)
                .addColumn(DOUBLE_TYPE, DOUBLE)
                .addColumn(STRING_TYPE, TEXT)
                .addColumn(TIMESTAMP_TYPE, TIMESTAMP)
                .create();

        createTableCommand.asIgnore();
    }

    static void dropTable(JDBCConnection jdbcConnection)
    {
        var dropTableCommand = jdbcConnection.createTableCommand(JDBCTestSchema.class).dropTableIfExists(JDBCTestDatabase.class);

        dropTableCommand.asIgnore();
    }

    static void insertTestData(JDBCConnection jdbcConnection)
    {
        var insertNullValues = jdbcConnection.createCommand(JDBCTestSchema.class)
                .insertInto(JDBCTestDatabase.class)
                .values(PRIMARY_KEY_WITH_NULL_VALUES, null, null, null, null, null, null )
                .create();

        var insertNonNullValues = jdbcConnection.createCommand(JDBCTestSchema.class)
                .insertInto(JDBCTestDatabase.class)
                .values(PRIMARY_KEY_WITH_NONNULL_VALUES, TEST_INT_VALUE, TEST_NUMERIC_VALUE, TEST_FLOAT_VALUE, TEST_DOUBLE_VALUE, TEST_STRING, TEST_TIMESTAMP)
                .create();

        var insertNonNullValuesDuplicate = jdbcConnection.createCommand(JDBCTestSchema.class)
                .insertInto(JDBCTestDatabase.class)
                .values(PRIMARY_KEY_WITH_NONNULL_VALUES_DUPLICATE, TEST_INT_VALUE, TEST_NUMERIC_VALUE, TEST_FLOAT_VALUE, TEST_DOUBLE_VALUE, TEST_STRING,
                        TEST_TIMESTAMP)
                .create();

        insertNullValues.asUpdate();
        insertNonNullValues.asUpdate();
        insertNonNullValuesDuplicate.asUpdate();
    }
}
