package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryBuilderTest.JDBCQueryBuilderTestSchema.INTEGER_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryBuilderTest.JDBCQueryBuilderTestSchema.JDBCQueryBuilderTestTable;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryBuilderTest.JDBCQueryBuilderTestSchema.KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JDBCQueryBuilderTest
{
    private static final int PRIMARY_KEY_WITH_NULL_VALUES = 1;
    private static final int PRIMARY_KEY_WITH_NONNULL_VALUES = 2;
    private static final int PRIMARY_KEY_NOT_PRESENT = 3;

    private final Timestamp testTimestamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MICROS));
    private final String testString = "Hello World";
    private final int testIntValue = 2;
    private final int testFloatValue = 3;
    private final int testDoubleValue = 4;
    private final BigDecimal testNumericValue = BigDecimal.valueOf(5);

    private JDBCConnection jdbcConnection;

    enum JDBCQueryBuilderTestSchema {
        JDBCQueryBuilderTestTable,
        KEY,
        INTEGER_TYPE,
        NUMERIC_TYPE,
        FLOAT_TYPE,
        DOUBLE_TYPE,
        STRING_TYPE,
        TIMESTAMP_TYPE
    }
    /* End < Sichtweise Entwickler auf die API */


    @BeforeEach
    void initTest()
    {
        var postgresProperties = new Properties();
        postgresProperties.put(JDBCConnection.JDBC_DRIVER, "org.postgresql.Driver");
        postgresProperties.put(JDBCConnection.JDBC_PASSWORD, "admin");
        postgresProperties.put(JDBCConnection.JDBC_USERNAME, "admin");
        postgresProperties.put(JDBCConnection.JDBC_URL, "jdbc:postgresql://localhost:5432/jexxa");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_TABLE, "true");
        postgresProperties.put(JDBCConnection.JDBC_AUTOCREATE_DATABASE, "jdbc:postgresql://localhost:5432/postgres");

        jdbcConnection = new JDBCConnection(postgresProperties);
        dropTable();
        autocreateTable();
        insertTestData();
    }

    /* Begin > Sichtweise Entwickler auf die API */
    @Test
    void buildQuery()
    {
        //"SELECT SEQID FROM bestellung WHERE bestid=5"
        var objectUnderTest = jdbcConnection.createQuery(JDBCQueryBuilderTestSchema.class);

        //Act
        var query = objectUnderTest
                .select(INTEGER_TYPE)
                .from(JDBCQueryBuilderTestTable)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        //Assert
        assertFalse(query.isEmpty());
        assertEquals(testIntValue, query.asInt().findFirst().orElseThrow() );
    }


    private void autocreateTable()
    {

        var command = String.format("CREATE TABLE IF NOT EXISTS %s ( key INTEGER PRIMARY KEY" +
                        ", integer_type integer" +
                        ", numeric_type numeric" +
                        ", float_type float" +
                        ", double_type double precision" +
                        ", string_type VARCHAR  " +
                        ", timestamp_type TIMESTAMP)"
                , JDBCQueryBuilderTestTable);

        jdbcConnection
                .execute(command)
                .asIgnore();


    }

    private void dropTable()
    {
        var command = String.format("DROP TABLE IF EXISTS %s ", JDBCQueryBuilderTestTable);

        jdbcConnection
                .execute(command)
                .asIgnore();
    }

    private void insertTestData()
    {
        var insertNullValues = String.format("insert into %s values( '%s' , null, null, null, null, null, null )",
                JDBCQueryBuilderTestTable, PRIMARY_KEY_WITH_NULL_VALUES);
        var insertNonNullValues = String.format("insert into %s values( '%s' , '%s', '%s', '%s', '%s', '%s' , '%s')",
                JDBCQueryBuilderTestTable, PRIMARY_KEY_WITH_NONNULL_VALUES, testIntValue , testNumericValue, testFloatValue, testDoubleValue, testString, testTimestamp);

        jdbcConnection.execute(insertNullValues).asUpdate();
        jdbcConnection.execute(insertNonNullValues).asUpdate();
    }


}
