package io.jexxa.common.wrapper.jdbc;

import io.jexxa.TestConstants;
import io.jexxa.common.facade.jdbc.JDBCConnection;
import io.jexxa.common.facade.jdbc.JDBCQuery;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JDBCFluentAPIIT
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

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testNonNullValues(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);
        createQueries();

        //act
        assertDoesNotThrow(() -> queryNonNullInteger.asInt().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNonNullNumeric.asNumeric().flatMap(Optional::stream).findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNonNullFloat.asFloat().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNonNullDouble.asDouble().findFirst().orElseThrow() );
        assertDoesNotThrow(() -> queryNonNullString.asString().flatMap(Optional::stream).findFirst().orElseThrow());
        assertDoesNotThrow(() -> queryNonNullTimestamp.asTimestamp().flatMap(Optional::stream).findFirst().orElseThrow());

        assertEquals(JDBCTestDatabase.TEST_INT_VALUE, queryNonNullInteger.asInt().findFirst().orElseThrow() );
        assertEquals(JDBCTestDatabase.TEST_NUMERIC_VALUE, queryNonNullNumeric.asNumeric().flatMap(Optional::stream).findFirst().orElseThrow() );
        assertEquals(JDBCTestDatabase.TEST_FLOAT_VALUE, queryNonNullFloat.asFloat().findFirst().orElseThrow() );
        assertEquals(JDBCTestDatabase.TEST_DOUBLE_VALUE, queryNonNullDouble.asDouble().findFirst().orElseThrow() );
        assertEquals(JDBCTestDatabase.TEST_STRING, queryNonNullString.asString().flatMap(Optional::stream).findFirst().orElseThrow());
        assertEquals(JDBCTestDatabase.TEST_TIMESTAMP, queryNonNullTimestamp.asTimestamp().flatMap(Optional::stream).findFirst().orElseThrow());

    }


    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testNullValues(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);
        createQueries();

        //act and assert
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

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testIsPresent(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);
        createQueries();

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

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testIsEmpty(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);
        createQueries();

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
                .select(JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullNumeric = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullFloat = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullDouble = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullString = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NULL_VALUES)
                .create();

        queryNullTimestamp = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NULL_VALUES)
                .create();
    }

    private void createQueriesForNonNullValues()
    {
        queryNonNullInteger = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullNumeric = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullFloat = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullDouble = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullString = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        queryNonNullTimestamp = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();
    }
    private void createQueriesForEmptyValues()
    {
        queryNotAvailableInteger = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_VALUES_NOT_PRESENT)
                .create();

        queryNotAvailableString = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_VALUES_NOT_PRESENT)
                .create();
    }

}
