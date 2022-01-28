package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import io.jexxa.TestConstants;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Properties;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.JDBCTestSchema.STRING_TYPE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCTestDatabase.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        var deleteAllRowsQuery = jdbcConnection.createCommand(JDBCTestSchema.class)
                .deleteFrom(JDBCTestDatabase.class)
                .where(REPOSITORY_KEY).isNotEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .or(REPOSITORY_KEY).isEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        var validateNoEntriesQuery = jdbcConnection.createQuery(JDBCTestSchema.class)
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


        var updateQuery = jdbcConnection.createCommand(JDBCTestSchema.class) //Simulate an equal statement
                .update(JDBCTestDatabase.class)
                .set(STRING_TYPE, new JDBCObject( updatedString, SQLDataType.TEXT ))
                .where(REPOSITORY_KEY).isGreaterOrEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .and(REPOSITORY_KEY).isLessOrEqual(PRIMARY_KEY_WITH_NONNULL_VALUES)
                .create();

        var validateUpdate = jdbcConnection.createQuery(JDBCTestSchema.class)
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
