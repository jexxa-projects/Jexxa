package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;

import io.jexxa.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestConstants.INTEGRATION_TEST)
class JDBCQueryTest
{
    private static final int PRIMARY_KEY_WITH_NULL_VALUES = 1;
    private static final int PRIMARY_KEY_WITH_NONNULL_VALUES = 2;
    private static final int PRIMARY_KEY_NOT_PRESENT = 3;

    private final String queryNullInteger =  String.format("select integer_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NULL_VALUES);
    private final String queryNullNumeric =  String.format("select numeric_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NULL_VALUES);
    private final String queryNullFloat =  String.format("select float_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NULL_VALUES);
    private final String queryNullDouble =  String.format("select double_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NULL_VALUES);
    private final String queryNullString =  String.format("select string_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NULL_VALUES);
    private final String queryNullTimestamp =  String.format("select timestamp_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NULL_VALUES);

    private final String queryNonNullInteger =  String.format("select integer_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NONNULL_VALUES);
    private final String queryNonNullNumeric =  String.format("select numeric_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NONNULL_VALUES);
    private final String queryNonNullFloat =  String.format("select float_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NONNULL_VALUES);
    private final String queryNonNullDouble =  String.format("select double_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NONNULL_VALUES);
    private final String queryNonNullString =  String.format("select string_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NONNULL_VALUES);
    private final String queryNonNullTimestamp =  String.format("select timestamp_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NONNULL_VALUES);


    private final String queryNotAvailableInteger =  String.format("select integer_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_NOT_PRESENT);
    private final String queryNotAvailableString =  String.format("select string_type from %s where key = '%s'", JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_NOT_PRESENT);

    private final Timestamp testTimestamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MICROS));
    private final String testString = "Hello World";
    private final int testIntValue = 2;
    private final int testFloatValue = 3;
    private final int testDoubleValue = 4;
    private final BigDecimal testNumericValue = BigDecimal.valueOf(5);

    private JDBCConnection jdbcConnection;

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

    @Test
    void testNonNullValues()
    {
        //Arrange

        //act
        assertDoesNotThrow(() -> jdbcConnection.query(queryNonNullInteger).asInt().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> jdbcConnection.query(queryNonNullNumeric).asNumeric().flatMap(Optional::stream).findFirst().orElseThrow() );
        assertDoesNotThrow(() -> jdbcConnection.query(queryNonNullFloat).asFloat().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> jdbcConnection.query(queryNonNullDouble).asDouble().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> jdbcConnection.query(queryNonNullString).asString().flatMap(Optional::stream).findFirst().orElseThrow());
        assertDoesNotThrow(() -> jdbcConnection.query(queryNonNullTimestamp).asTimestamp().flatMap(Optional::stream).findFirst().orElseThrow());

        assertEquals(testIntValue, jdbcConnection.query(queryNonNullInteger).asInt().findFirst().orElseThrow() );
        assertEquals(testNumericValue, jdbcConnection.query(queryNonNullNumeric).asNumeric().flatMap(Optional::stream).findFirst().orElseThrow() );
        assertEquals(testFloatValue, jdbcConnection.query(queryNonNullFloat).asFloat().findFirst().orElseThrow() );
        assertEquals(testDoubleValue, jdbcConnection.query(queryNonNullDouble).asDouble().findFirst().orElseThrow() );
        assertEquals(testString, jdbcConnection.query(queryNonNullString).asString().flatMap(Optional::stream).findFirst().orElseThrow());
        assertEquals(testTimestamp, jdbcConnection.query(queryNonNullTimestamp).asTimestamp().flatMap(Optional::stream).findFirst().orElseThrow());

    }


    @Test
    void testNullValues()
    {
        //Arrange - nothing

        //act / assert
        assertDoesNotThrow(() -> jdbcConnection.query(queryNullInteger).asInt().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> jdbcConnection.query(queryNullNumeric).asNumeric().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> jdbcConnection.query(queryNullFloat).asFloat().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> jdbcConnection.query(queryNullDouble).asDouble().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> jdbcConnection.query(queryNullString).asString().findFirst().orElseThrow());
        assertDoesNotThrow(() -> jdbcConnection.query(queryNullTimestamp).asTimestamp().findFirst().orElseThrow());

        assertEquals(0, jdbcConnection.query(queryNullInteger).asInt().findFirst().orElseThrow() );
        assertEquals(Optional.empty(), jdbcConnection.query(queryNullNumeric).asNumeric().findFirst().orElseThrow() );
        assertEquals(0, jdbcConnection.query(queryNullFloat).asFloat().findFirst().orElseThrow() );
        assertEquals(0, jdbcConnection.query(queryNullDouble).asDouble().findFirst().orElseThrow() );
        assertEquals(Optional.empty(), jdbcConnection.query(queryNullString).asString().findFirst().orElseThrow());
        assertEquals(Optional.empty(), jdbcConnection.query(queryNullTimestamp).asTimestamp().findFirst().orElseThrow());
    }

    @Test
    void testIsPresent()
    {
        //Arrange - nothing

        //act / assert - NullValues
        assertTrue( jdbcConnection.query(queryNullInteger).isPresent());
        assertTrue( jdbcConnection.query(queryNullNumeric).isPresent());
        assertTrue( jdbcConnection.query(queryNullFloat).isPresent());
        assertTrue( jdbcConnection.query(queryNullDouble).isPresent());
        assertTrue( jdbcConnection.query(queryNullString).isPresent());
        assertTrue( jdbcConnection.query(queryNullTimestamp).isPresent());

        //act / assert - NonNullValues
        assertTrue( jdbcConnection.query(queryNonNullInteger).isPresent());
        assertTrue( jdbcConnection.query(queryNonNullNumeric).isPresent());
        assertTrue( jdbcConnection.query(queryNonNullFloat).isPresent());
        assertTrue( jdbcConnection.query(queryNonNullDouble).isPresent());
        assertTrue( jdbcConnection.query(queryNonNullString).isPresent());
        assertTrue( jdbcConnection.query(queryNullTimestamp).isPresent());

        //act / assert - Not available values
        assertFalse( jdbcConnection.query(queryNotAvailableInteger).isPresent());
        assertFalse( jdbcConnection.query(queryNotAvailableString).isPresent());
    }

    @Test
    void testIsEmpty()
    {
        //Arrange - nothing

        //act / assert - NullValues
        assertFalse( jdbcConnection.query(queryNullInteger).isEmpty());
        assertFalse( jdbcConnection.query(queryNullNumeric).isEmpty());
        assertFalse( jdbcConnection.query(queryNullFloat).isEmpty());
        assertFalse( jdbcConnection.query(queryNullDouble).isEmpty());
        assertFalse( jdbcConnection.query(queryNullString).isEmpty());
        assertFalse( jdbcConnection.query(queryNullTimestamp).isEmpty());

        //act / assert - NonNullValues
        assertFalse( jdbcConnection.query(queryNonNullInteger).isEmpty());
        assertFalse( jdbcConnection.query(queryNonNullNumeric).isEmpty());
        assertFalse( jdbcConnection.query(queryNonNullFloat).isEmpty());
        assertFalse( jdbcConnection.query(queryNonNullDouble).isEmpty());
        assertFalse( jdbcConnection.query(queryNonNullString).isEmpty());
        assertFalse( jdbcConnection.query(queryNullTimestamp).isEmpty());

        //act / assert - Not available values
        assertTrue( jdbcConnection.query(queryNotAvailableInteger).isEmpty());
        assertTrue( jdbcConnection.query(queryNotAvailableString).isEmpty());
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
                , JDBCQueryTest.class.getSimpleName());

        jdbcConnection
                .execute(command)
                .asIgnore();


    }

    private void dropTable()
    {
        var command = String.format("DROP TABLE IF EXISTS %s ", JDBCQueryTest.class.getSimpleName());

        jdbcConnection
                .execute(command)
                .asIgnore();
    }

    private void insertTestData()
    {
        var insertNullValues = String.format("insert into %s values( '%s' , null, null, null, null, null, null )",
                JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NULL_VALUES);
        var insertNonNullValues = String.format("insert into %s values( '%s' , '%s', '%s', '%s', '%s', '%s' , '%s')",
                JDBCQueryTest.class.getSimpleName(), PRIMARY_KEY_WITH_NONNULL_VALUES, testIntValue , testNumericValue, testFloatValue, testDoubleValue, testString, testTimestamp);

        jdbcConnection.execute(insertNullValues).asUpdate();
        jdbcConnection.execute(insertNonNullValues).asUpdate();
    }


}
