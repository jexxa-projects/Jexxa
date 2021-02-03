package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.DOUBLE_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.FLOAT_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.INTEGER_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.NUMERIC_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.TIMESTAMP_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.autocreateTable;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.dropTable;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.insertTestData;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Stream;

import io.jexxa.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag(TestConstants.INTEGRATION_TEST)
class JDBCQueryTest
{
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
    }

    @Test
    void testSelectOR()
    {
        //Arrange
        var querySelectOr = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY).isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .or(STRING_TYPE).isNull()
                .create();

        var result = querySelectOr.asString();

        assertEquals(2, result.count());
    }

    @Test
    void testSelectAND()
    {
        //Arrange
        var querySelectAnd = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY).isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .and(STRING_TYPE).isNull()
                .create();

        var result = querySelectAnd.asString();

        assertEquals(0, result.count());
    }

    @Test
    void testMultiSelect()
    {
        //Arrange
        var queryMultiSelect = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .select(STRING_TYPE, INTEGER_TYPE)
                .from(JDBCTestDatabase.class)
                .where(KEY)
                .isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        var result = queryMultiSelect.as(this::readMultiSelect);

        assertEquals(2, result.findFirst().orElseThrow().count());
    }

    @Test
    void testSelectAll()
    {
        //Arrange
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
