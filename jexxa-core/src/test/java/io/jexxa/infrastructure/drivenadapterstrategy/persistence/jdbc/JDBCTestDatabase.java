package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType.DOUBLE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType.FLOAT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType.INTEGER;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType.NUMERIC;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType.TEXT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType.TIMESTAMP;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class JDBCTestDatabase
{
    static final int PRIMARY_KEY_WITH_NULL_VALUES = 1;
    static final int PRIMARY_KEY_WITH_NONNULL_VALUES = 2;
    static final int PRIMARY_KEY_VALUES_NOT_PRESENT = 3;
    static final Timestamp testTimestamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MICROS));
    static final String testString = "Hello World";
    static final int testIntValue = 2;
    static final int testFloatValue = 3;
    static final int testDoubleValue = 4;
    static final BigDecimal testNumericValue = BigDecimal.valueOf(5);


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

    static void autocreateTable(JDBCConnection jdbcConnection)
    {
        var createTableCommand = jdbcConnection.createCommand(JDBCTestSchema.class)
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
        var dropTableCommand = jdbcConnection.createCommand(JDBCTestSchema.class).dropTableIfExists(JDBCTestDatabase.class);

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
                .values(PRIMARY_KEY_WITH_NONNULL_VALUES, testIntValue , testNumericValue, testFloatValue, testDoubleValue, testString, testTimestamp)
                .create();

        insertNullValues.asUpdate();
        insertNonNullValues.asUpdate();
    }
}
