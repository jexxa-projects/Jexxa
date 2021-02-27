package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.REPOSITORY_CONFIG;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.setupDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Ordering;
import io.jexxa.TestConstants;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag(TestConstants.INTEGRATION_TEST)
@Execution(ExecutionMode.SAME_THREAD)
class JDBCQueryIT
{
    private JDBCConnection jdbcConnection;

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testSelectOR(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectOr = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY).isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .or(STRING_TYPE).isNull()
                .create();

        var result = querySelectOr.asString();

        assertEquals(2, result.count());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testSelectAND(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectAnd = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY).isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .and(STRING_TYPE).isNull()
                .create();

        var result = querySelectAnd.asString();

        assertEquals(0, result.count());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testMultiSelect(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var queryMultiSelect = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE, INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        var result = queryMultiSelect.as(this::readMultiSelect);

        assertEquals(2, result.findFirst().orElseThrow().count());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testSelectAll(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectAll = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectAll()
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        //Act
        var result = querySelectAll.as(this::readSelectAll);

        //Assert
        assertEquals(7, result.findFirst().orElseThrow().count());
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testSelectAsc(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectAsc = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(KEY)
                .from(JDBCTestDatabase.class)
                .orderBy(KEY, SQLOrder.ASC)
                .create();

        //Act
        var result = querySelectAsc.asInt().collect(Collectors.toList());

        //Assert
        assertEquals(3, result.size());

        assertFalse(Ordering.natural().reverse().isOrdered(result));
        assertTrue(Ordering.natural().isOrdered(result));
    }

    @ParameterizedTest
    @MethodSource(REPOSITORY_CONFIG)
    void testSelectDesc(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectDesc = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(KEY)
                .from(JDBCTestDatabase.class)
                .orderBy(KEY, SQLOrder.DESC)
                .create();

        //Act
        var result = querySelectDesc.asInt().collect(Collectors.toList());

        //Assert
        assertEquals(3, result.size());

        assertTrue(Ordering.natural().reverse().isOrdered(result));
        assertFalse(Ordering.natural().isOrdered(result));
    }

    // Begin> Utility methods used in this test

    private Stream<String> readMultiSelect(ResultSet resultSet ) throws SQLException
    {
        return Stream.of(
                resultSet.getString(STRING_TYPE.name()),
                String.valueOf( resultSet.getInt(INTEGER_TYPE.name()))
        );
    }

    private Stream<String> readSelectAll(ResultSet resultSet ) throws SQLException
    {
        return Stream.of(
                String.valueOf( resultSet.getInt(KEY.name())),
                resultSet.getString(STRING_TYPE.name()),
                String.valueOf( resultSet.getInt(INTEGER_TYPE.name())),
                String.valueOf( resultSet.getFloat(FLOAT_TYPE.name())),
                String.valueOf( resultSet.getDouble(DOUBLE_TYPE.name())),
                String.valueOf( resultSet.getBigDecimal(NUMERIC_TYPE.name())),
                String.valueOf( resultSet.getTimestamp(TIMESTAMP_TYPE.name()))
        );
    }


}
