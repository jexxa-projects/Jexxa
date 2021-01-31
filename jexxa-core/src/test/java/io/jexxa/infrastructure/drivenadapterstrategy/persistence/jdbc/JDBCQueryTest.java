package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryTest.JDBCQueryTestSchema.DOUBLE_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryTest.JDBCQueryTestSchema.FLOAT_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryTest.JDBCQueryTestSchema.INTEGER_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryTest.JDBCQueryTestSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryTest.JDBCQueryTestSchema.NUMERIC_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryTest.JDBCQueryTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQueryTest.JDBCQueryTestSchema.TIMESTAMP_TYPE;
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

    private JDBCPreparedQuery queryNullInteger;
    private JDBCPreparedQuery queryNullNumeric;
    private JDBCPreparedQuery queryNullFloat;
    private JDBCPreparedQuery queryNullDouble;
    private JDBCPreparedQuery queryNullString;
    private JDBCPreparedQuery queryNullTimestamp;

    private JDBCPreparedQuery queryNonNullInteger;
    private JDBCPreparedQuery queryNonNullNumeric;
    private JDBCPreparedQuery queryNonNullFloat;
    private JDBCPreparedQuery queryNonNullDouble;
    private JDBCPreparedQuery queryNonNullString;
    private JDBCPreparedQuery queryNonNullTimestamp;


    private JDBCPreparedQuery queryNotAvailableInteger;
    private JDBCPreparedQuery queryNotAvailableString;


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
        createQueries();
    }

    @Test
    void testNonNullValues()
    {
        //Arrange

        //act
        assertDoesNotThrow(() -> queryNonNullInteger.asInt().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNonNullNumeric.asNumeric().flatMap(Optional::stream).findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNonNullFloat.asFloat().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNonNullDouble.asDouble().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNonNullString.asString().flatMap(Optional::stream).findFirst().orElseThrow());
        assertDoesNotThrow(() -> queryNonNullTimestamp.asTimestamp().flatMap(Optional::stream).findFirst().orElseThrow());

        assertEquals(testIntValue, queryNonNullInteger.asInt().findFirst().orElseThrow() );
        assertEquals(testNumericValue, queryNonNullNumeric.asNumeric().flatMap(Optional::stream).findFirst().orElseThrow() );
        assertEquals(testFloatValue, queryNonNullFloat.asFloat().findFirst().orElseThrow() );
        assertEquals(testDoubleValue, queryNonNullDouble.asDouble().findFirst().orElseThrow() );
        assertEquals(testString, queryNonNullString.asString().flatMap(Optional::stream).findFirst().orElseThrow());
        assertEquals(testTimestamp, queryNonNullTimestamp.asTimestamp().flatMap(Optional::stream).findFirst().orElseThrow());

    }


    @Test
    void testNullValues()
    {
        //Arrange - nothing

        //act / assert
        assertDoesNotThrow(() -> queryNullInteger.asInt().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNullNumeric.asNumeric().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNullFloat.asFloat().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNullDouble.asDouble().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNullString.asString().findFirst().orElseThrow());
        assertDoesNotThrow(() -> queryNullTimestamp.asTimestamp().findFirst().orElseThrow());

        assertEquals(0, queryNullInteger.asInt().findFirst().orElseThrow() );
        assertEquals(Optional.empty(), queryNullNumeric.asNumeric().findFirst().orElseThrow() );
        assertEquals(0, queryNullFloat.asFloat().findFirst().orElseThrow() );
        assertEquals(0, queryNullDouble.asDouble().findFirst().orElseThrow() );
        assertEquals(Optional.empty(), queryNullString.asString().findFirst().orElseThrow());
        assertEquals(Optional.empty(), queryNullTimestamp.asTimestamp().findFirst().orElseThrow());
    }

    @Test
    void testIsPresent()
    {
        //Arrange - nothing

        //act / assert - NullValues
        assertTrue( queryNullInteger.isPresent());
        assertTrue( queryNullNumeric.isPresent());
        assertTrue( queryNullFloat.isPresent());
        assertTrue( queryNullDouble.isPresent());
        assertTrue( queryNullString.isPresent());
        assertTrue( queryNullTimestamp.isPresent());

        //act / assert - NonNullValues
        assertTrue( queryNonNullInteger.isPresent());
        assertTrue( queryNonNullNumeric.isPresent());
        assertTrue( queryNonNullFloat.isPresent());
        assertTrue( queryNonNullDouble.isPresent());
        assertTrue( queryNonNullString.isPresent());
        assertTrue( queryNullTimestamp.isPresent());

        //act / assert - Not available values
        assertFalse( queryNotAvailableInteger.isPresent());
        assertFalse( queryNotAvailableString.isPresent());
    }

    @Test
    void testIsEmpty()
    {
        //Arrange - nothing

        //act / assert - NullValues
        assertFalse( queryNullInteger.isEmpty());
        assertFalse( queryNullNumeric.isEmpty());
        assertFalse( queryNullFloat.isEmpty());
        assertFalse( queryNullDouble.isEmpty());
        assertFalse( queryNullString.isEmpty());
        assertFalse( queryNullTimestamp.isEmpty());

        //act / assert - NonNullValues
        assertFalse( queryNonNullInteger.isEmpty());
        assertFalse( queryNonNullNumeric.isEmpty());
        assertFalse( queryNonNullFloat.isEmpty());
        assertFalse( queryNonNullDouble.isEmpty());
        assertFalse( queryNonNullString.isEmpty());
        assertFalse( queryNullTimestamp.isEmpty());

        //act / assert - Not available values
        assertTrue( queryNotAvailableInteger.isEmpty());
        assertTrue( queryNotAvailableString.isEmpty());
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

       /* var createTableCommand = jdbcConnection.createCommand(JDBCQueryTestSchema.class)
                .createTableIfNotExists(JDBCQueryTest.class)
                .addColumn(KEY, INTEGER, PRIMARY_KEY)
                .addColumn(KEY, INTEGER)
                .addColumn(KEY, INTEGER)
                .create();
         */

        jdbcConnection
                .execute(command)
                .asIgnore();


    }

    private void dropTable()
    {
        var dropTableCommand = jdbcConnection.createCommand(JDBCQueryTestSchema.class).dropTableIfExists(JDBCQueryTest.class);

        dropTableCommand.asIgnore();
    }

    private void insertTestData()
    {
        var insertNullValues = jdbcConnection.createCommand(JDBCQueryTestSchema.class)
                .insertInto(JDBCQueryTest.class)
                .values(PRIMARY_KEY_WITH_NULL_VALUES, null, null, null, null, null, null )
                .create();

        var insertNonNullValues = jdbcConnection.createCommand(JDBCBuilderTest.JDBCBuilderTestSchema.class)
                .insertInto(JDBCQueryTest.class)
                .values(PRIMARY_KEY_WITH_NONNULL_VALUES, testIntValue , testNumericValue, testFloatValue, testDoubleValue, testString, testTimestamp)
                .create();

        insertNullValues.asUpdate();
        insertNonNullValues.asUpdate();
    }

    private void createQueries()
    {
        createQueriesForNullValues();
        createQueriesForNonNullValues();
        createQueriesForEmptyValues();
    }
    enum JDBCQueryTestSchema
    {
        KEY,
        INTEGER_TYPE,
        NUMERIC_TYPE,
        FLOAT_TYPE,
        DOUBLE_TYPE,
        STRING_TYPE,
        TIMESTAMP_TYPE
    }

    private void createQueriesForNullValues()
    {
        queryNullInteger = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(INTEGER_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullNumeric = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(NUMERIC_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullFloat = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(FLOAT_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullDouble = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(DOUBLE_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullString = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullTimestamp = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(TIMESTAMP_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();
    }

    void createQueriesForNonNullValues()
    {
        queryNonNullInteger = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(INTEGER_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullNumeric = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(NUMERIC_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullFloat = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(FLOAT_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullDouble = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(DOUBLE_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullString = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullTimestamp = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(TIMESTAMP_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();
    }
    private void createQueriesForEmptyValues()
    {
        queryNotAvailableInteger = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(INTEGER_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_NOT_PRESENT)
                .create();

        queryNotAvailableString = jdbcConnection.createQuery(JDBCQueryTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCQueryTest.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_NOT_PRESENT)
                .create();
    }
}
