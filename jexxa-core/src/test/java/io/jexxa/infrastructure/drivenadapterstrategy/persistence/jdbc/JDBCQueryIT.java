package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBC_REPOSITORY_CONFIG;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.setupDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testSelectOR(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectOr = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(REPOSITORY_KEY).isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .or(STRING_TYPE).isNull()
                .create();

        var result = querySelectOr.asString();

        assertEquals(2, result.count());
    }

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testSelectAND(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectAnd = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(REPOSITORY_KEY).isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .and(STRING_TYPE).isNull()
                .create();

        var result = querySelectAnd.asString();

        assertEquals(0, result.count());
    }

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testMultiSelect(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var queryMultiSelect = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE, INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(REPOSITORY_KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        var result = queryMultiSelect.as(this::readMultiSelect);

        assertEquals(2, result.findFirst().orElseThrow().count());
    }

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testSelectAll(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectAll = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectAll()
                .from(JDBCTestDatabase.class)
                .where(REPOSITORY_KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        //Act
        var result = querySelectAll.as(this::readSelectAll);

        //Assert
        assertEquals(7, result.findFirst().orElseThrow().count());
    }

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testSelectCount(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectAll = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectCount()
                .from(JDBCTestDatabase.class)
                .create();

        //Act
        var result = querySelectAll.asInt();

        //Assert
        assertEquals(3, result.findFirst().orElseThrow());
    }

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testSelectCountParameter(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectAll = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectCount(REPOSITORY_KEY)
                .from(JDBCTestDatabase.class)
                .create();

        //Act
        var result = querySelectAll.asInt();

        //Assert
        assertEquals(3, result.findFirst().orElseThrow());
    }

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testSelectCountEmptyTable(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);
        var command = jdbcConnection.createCommand(JDBCTestDatabase.JDBCTestSchema.class)
                .deleteFrom(JDBCTestDatabase.class)
                .create();

        command.asIgnore();

        var querySelectAll = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectCount()
                .from(JDBCTestDatabase.class)
                .create();

        //Act
        var result = querySelectAll.asInt();

        //Assert
        assertEquals(0, result.findFirst().orElseThrow());
    }

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testSelectAsc(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectAsc = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(REPOSITORY_KEY)
                .from(JDBCTestDatabase.class)
                .orderBy(REPOSITORY_KEY, SQLOrder.ASC)
                .create();

        //Act
        var result = querySelectAsc.asInt().collect(Collectors.toList());

        //Assert
        assertEquals(3, result.size());

        assertTrue(isSorted(result));
        assertFalse(isReverseSorted(result));
    }

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testSelectDesc(Properties properties)
    {
        //Arrange
        jdbcConnection = setupDatabase(properties);

        var querySelectDesc = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(REPOSITORY_KEY)
                .from(JDBCTestDatabase.class)
                .orderBy(REPOSITORY_KEY, SQLOrder.DESC)
                .create();

        //Act
        var result = querySelectDesc.asInt().collect(Collectors.toList());

        //Assert
        assertEquals(3, result.size());

        assertTrue(isReverseSorted(result));
        assertFalse(isSorted(result));
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
                String.valueOf( resultSet.getInt(REPOSITORY_KEY.name())),
                resultSet.getString(STRING_TYPE.name()),
                String.valueOf( resultSet.getInt(INTEGER_TYPE.name())),
                String.valueOf( resultSet.getFloat(FLOAT_TYPE.name())),
                String.valueOf( resultSet.getDouble(DOUBLE_TYPE.name())),
                String.valueOf( resultSet.getBigDecimal(NUMERIC_TYPE.name())),
                String.valueOf( resultSet.getTimestamp(TIMESTAMP_TYPE.name()))
        );
    }

    boolean isSorted(List<Integer> intList)
    {
        var sortedList = new ArrayList<>(intList);
        Collections.sort(sortedList);

        return intList.equals(sortedList);
    }

    boolean isReverseSorted(List<Integer> intList)
    {
        var reverseSortedList = new ArrayList<>(intList);
        reverseSortedList.sort(Collections.reverseOrder());

        return intList.equals(reverseSortedList);
    }
}
