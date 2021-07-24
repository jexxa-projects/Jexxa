package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBC_REPOSITORY_CONFIG;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.setupDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;

import io.jexxa.TestConstants;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JDBCCommandIT
{
    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testDeleteValues(Properties properties)
    {
        //arrange
        var jdbcConnection = setupDatabase(properties);

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

    @ParameterizedTest
    @MethodSource(JDBC_REPOSITORY_CONFIG)
    void testUpdateValues(Properties properties)
    {
        //arrange
        String updatedString = "UpdatesString";
        var jdbcConnection = setupDatabase(properties);


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
