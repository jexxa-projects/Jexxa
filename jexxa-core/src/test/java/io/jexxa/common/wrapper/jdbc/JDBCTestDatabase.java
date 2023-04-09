package io.jexxa.common.wrapper.jdbc;

import io.jexxa.common.wrapper.jdbc.builder.JDBCTableBuilder;
import io.jexxa.common.wrapper.jdbc.builder.SQLDataType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.stream.Stream;

import static io.jexxa.common.wrapper.jdbc.JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE;
import static io.jexxa.common.wrapper.jdbc.JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE;
import static io.jexxa.common.wrapper.jdbc.JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE;
import static io.jexxa.common.wrapper.jdbc.JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE;
import static io.jexxa.common.wrapper.jdbc.JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY;
import static io.jexxa.common.wrapper.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.common.wrapper.jdbc.JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE;
import static io.jexxa.infrastructure.persistence.RepositoryConfig.jdbcRepositoryConfig;

public final class JDBCTestDatabase
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
        REPOSITORY_KEY,
        INTEGER_TYPE,
        NUMERIC_TYPE,
        FLOAT_TYPE,
        DOUBLE_TYPE,
        STRING_TYPE,
        TIMESTAMP_TYPE,
    }

    public static final String JDBC_REPOSITORY_CONFIG = "io.jexxa.common.wrapper.jdbc.JDBCTestDatabase#repositoryConfigJDBC";

    @SuppressWarnings("unused")
    public static Stream<Properties> repositoryConfigJDBC() {
        return jdbcRepositoryConfig("jexxa");
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
                .addColumn(REPOSITORY_KEY, SQLDataType.INTEGER)
                .addConstraint(JDBCTableBuilder.SQLConstraint.PRIMARY_KEY)
                .addColumn(INTEGER_TYPE, SQLDataType.INTEGER)
                .addColumn(NUMERIC_TYPE, SQLDataType.NUMERIC)
                .addColumn(FLOAT_TYPE, SQLDataType.FLOAT)
                .addColumn(DOUBLE_TYPE, SQLDataType.DOUBLE)
                .addColumn(STRING_TYPE, SQLDataType.TEXT)
                .addColumn(TIMESTAMP_TYPE, SQLDataType.TIMESTAMP)
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
                .values(new Object[]{PRIMARY_KEY_WITH_NULL_VALUES, null, null, null, null, null, null} )
                .create();

        var insertNonNullValues = jdbcConnection.createCommand(JDBCTestSchema.class)
                .insertInto(JDBCTestDatabase.class)
                .values(new Object[]{PRIMARY_KEY_WITH_NONNULL_VALUES, TEST_INT_VALUE, TEST_NUMERIC_VALUE, TEST_FLOAT_VALUE, TEST_DOUBLE_VALUE, TEST_STRING, TEST_TIMESTAMP})
                .create();

        var insertNonNullValuesDuplicate = jdbcConnection.createCommand(JDBCTestSchema.class)
                .insertInto(JDBCTestDatabase.class)
                .values(new Object[]{PRIMARY_KEY_WITH_NONNULL_VALUES_DUPLICATE, TEST_INT_VALUE, TEST_NUMERIC_VALUE, TEST_FLOAT_VALUE, TEST_DOUBLE_VALUE, TEST_STRING,
                        TEST_TIMESTAMP})
                .create();

        insertNullValues.asUpdate();
        insertNonNullValues.asUpdate();
        insertNonNullValuesDuplicate.asUpdate();
    }

    private JDBCTestDatabase()
    {
        //private constructor
    }
}
