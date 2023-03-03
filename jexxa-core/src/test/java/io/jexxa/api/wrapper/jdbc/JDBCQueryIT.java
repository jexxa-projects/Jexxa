package io.jexxa.api.wrapper.jdbc;

import io.jexxa.TestConstants;
import io.jexxa.api.wrapper.jdbc.builder.SQLOrder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag(TestConstants.INTEGRATION_TEST)
@Execution(ExecutionMode.SAME_THREAD)
class JDBCQueryIT
{
    private JDBCConnection jdbcConnection;

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testSelectOR(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);

        var querySelectOr = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY).isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .or(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE).isNull()
                .create();

        var result = querySelectOr.asString();

        assertEquals(2, result.count());
    }

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testSelectAND(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);

        var querySelectAnd = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY).isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .and(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE).isNull()
                .create();

        var result = querySelectAnd.asString();

        assertEquals(0, result.count());
    }

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testMultiSelect(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);

        var queryMultiSelect = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE, JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        var result = queryMultiSelect.as(this::readMultiSelect);

        assertEquals(2, result.findFirst().orElseThrow().count());
    }

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testSelectAll(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);

        var querySelectAll = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectAll()
                .from(JDBCTestDatabase.class)
                .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        //Act
        var result = querySelectAll.as(this::readSelectAll);

        //Assert
        assertEquals(7, result.findFirst().orElseThrow().count());
    }

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testSelectCount(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);

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
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testSelectCountParameter(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);

        var querySelectAll = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectCount(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .from(JDBCTestDatabase.class)
                .create();

        //Act
        var result = querySelectAll.asInt();

        //Assert
        assertEquals(3, result.findFirst().orElseThrow());
    }

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testSelectCountEmptyTable(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);
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
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testSelectAsc(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);

        var querySelectAsc = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .from(JDBCTestDatabase.class)
                .orderBy(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY, SQLOrder.ASC)
                .create();

        //Act
        var result = querySelectAsc.asInt().toList();

        //Assert
        assertEquals(3, result.size());

        assertTrue(isSorted(result));
        assertFalse(isReverseSorted(result));
    }

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testSelectDesc(Properties properties)
    {
        //Arrange
        jdbcConnection = JDBCTestDatabase.setupDatabase(properties);

        var querySelectDesc = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY)
                .from(JDBCTestDatabase.class)
                .orderBy(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY, SQLOrder.DESC)
                .create();

        //Act
        var result = querySelectDesc.asInt().toList();

        //Assert
        assertEquals(3, result.size());

        assertTrue(isReverseSorted(result));
        assertFalse(isSorted(result));
    }

    // Begin> Utility methods used in this test

    private Stream<String> readMultiSelect(ResultSet resultSet ) throws SQLException
    {
        return Stream.of(
                resultSet.getString(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE.name()),
                String.valueOf( resultSet.getInt(JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE.name()))
        );
    }

    private Stream<String> readSelectAll(ResultSet resultSet ) throws SQLException
    {
        return Stream.of(
                String.valueOf( resultSet.getInt(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY.name())),
                resultSet.getString(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE.name()),
                String.valueOf( resultSet.getInt(JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE.name())),
                String.valueOf( resultSet.getFloat(JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE.name())),
                String.valueOf( resultSet.getDouble(JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE.name())),
                String.valueOf( resultSet.getBigDecimal(JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE.name())),
                String.valueOf( resultSet.getTimestamp(JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE.name()))
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
