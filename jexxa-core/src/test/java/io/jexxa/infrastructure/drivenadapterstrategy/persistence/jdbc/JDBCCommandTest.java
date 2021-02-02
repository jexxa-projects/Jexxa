package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.autocreateTable;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.dropTable;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.insertTestData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JDBCCommandTest
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
    void testDeleteValues()
    {
        //arrange
        var deleteAllRowsQuery = jdbcConnection.createCommand(JDBCTestDatabase.JDBCTestSchema.class)
                .deleteFrom(JDBCTestDatabase.class)
                .where(KEY).isNotEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .or(KEY).isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        var validateNoEntriesQuery = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectAll()
                .from(JDBCTestDatabase.class)
                .create();
        //act
        deleteAllRowsQuery.asUpdate();

        //Assert
        assertTrue(validateNoEntriesQuery.isEmpty());
    }

    @Test
    void testUpdateValues()
    {
        //arrange
        String updatedString = "UpdatesString";

        var updateQuery = jdbcConnection.createCommand(JDBCTestDatabase.JDBCTestSchema.class) //Simulate an equal statement
                .update(JDBCTestDatabase.class)
                .set(STRING_TYPE, updatedString)
                .where(KEY).isGreaterOrEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .and(KEY).isLessOrEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        var validateUpdate = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                .selectAll()
                .from(JDBCTestDatabase.class)
                .where(STRING_TYPE).isEqual(updatedString)
                .create();
        //act
        updateQuery.asUpdate();

        //Assert
        assertEquals(1, validateUpdate.asString().count());
    }


}
