package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.PRIMARY_KEY_VALUES_NOT_PRESENT;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.PRIMARY_KEY_WITH_NULL_VALUES;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.autocreateTable;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.dropTable;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.insertTestData;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.testDoubleValue;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.testFloatValue;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.testIntValue;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.testNumericValue;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.testString;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.testTimestamp;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import io.jexxa.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestConstants.INTEGRATION_TEST)
class JDBCFluentAPITest
{
    private JDBCQuery queryNullInteger;
    private JDBCQuery queryNullNumeric;
    private JDBCQuery queryNullFloat;
    private JDBCQuery queryNullDouble;
    private JDBCQuery queryNullString;
    private JDBCQuery queryNullTimestamp;

    private JDBCQuery queryNonNullInteger;
    private JDBCQuery queryNonNullNumeric;
    private JDBCQuery queryNonNullFloat;
    private JDBCQuery queryNonNullDouble;
    private JDBCQuery queryNonNullString;
    private JDBCQuery queryNonNullTimestamp;


    private JDBCQuery queryNotAvailableInteger;
    private JDBCQuery queryNotAvailableString;



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
        dropTable(jdbcConnection);
        autocreateTable(jdbcConnection);
        insertTestData(jdbcConnection);
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

    // Begin> Utility methods used in this test

    private void createQueries()
    {
        createQueriesForNullValues();
        createQueriesForNonNullValues();
        createQueriesForEmptyValues();
    }


    private void createQueriesForNullValues()
    {
        queryNullInteger = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullNumeric = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(NUMERIC_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullFloat = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(FLOAT_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullDouble = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(DOUBLE_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullString = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullTimestamp = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(TIMESTAMP_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NULL_VALUES)
                .create();
    }

    private void createQueriesForNonNullValues()
    {
        queryNonNullInteger = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullNumeric = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(NUMERIC_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullFloat = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(FLOAT_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullDouble = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(DOUBLE_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullString = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullTimestamp = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(TIMESTAMP_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();
    }
    private void createQueriesForEmptyValues()
    {
        queryNotAvailableInteger = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_VALUES_NOT_PRESENT)
                .create();

        queryNotAvailableString = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_VALUES_NOT_PRESENT)
                .create();
    }

}
