package io.jexxa.common.wrapper.jdbc;

import io.jexxa.TestConstants;
import io.jexxa.common.wrapper.jdbc.builder.JDBCObject;
import io.jexxa.common.wrapper.jdbc.builder.SQLDataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.SAME_THREAD)
@Tag(TestConstants.INTEGRATION_TEST)
class JDBCCommandIT
{
    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testDeleteValues(Properties properties)
    {
        try (JDBCConnection jdbcConnection = JDBCTestDatabase.setupDatabase(properties))
        {

            //arrange
            var deleteAllRowsQuery = jdbcConnection.createCommand(JDBCTestDatabase.JDBCTestSchema.class)
                    .deleteFrom(JDBCTestDatabase.class)
                    .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY).isNotEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                    .or(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY).isEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                    .create();

            var validateNoEntriesQuery = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                    .selectAll()
                    .from(JDBCTestDatabase.class)
                    .create();

            //act
            deleteAllRowsQuery.asUpdate();

            //Assert
            Assertions.assertTrue(validateNoEntriesQuery.isEmpty());
        }
    }

    @ParameterizedTest
    @MethodSource(JDBCTestDatabase.JDBC_REPOSITORY_CONFIG)
    void testUpdateValues(Properties properties)
    {
        try (JDBCConnection jdbcConnection = JDBCTestDatabase.setupDatabase(properties))
        {
            //arrange
            String updatedString = "UpdatesString";

            var updateQuery = jdbcConnection.createCommand(JDBCTestDatabase.JDBCTestSchema.class) //Simulate an equal statement
                    .update(JDBCTestDatabase.class)
                    .set(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE, new JDBCObject( updatedString, SQLDataType.TEXT ))
                    .where(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY).isGreaterOrEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                    .and(JDBCTestDatabase.JDBCTestSchema.REPOSITORY_KEY).isLessOrEqual(JDBCTestDatabase.PRIMARY_KEY_WITH_NONNULL_VALUES)
                    .create();

            var validateUpdate = jdbcConnection.createQuery(JDBCTestDatabase.JDBCTestSchema.class)
                    .selectAll()
                    .from(JDBCTestDatabase.class)
                    .where(JDBCTestDatabase.JDBCTestSchema.STRING_TYPE).isEqual(updatedString)
                    .create();
            //act
            updateQuery.asUpdate();

            //Assert
            Assertions.assertEquals(1, validateUpdate.asString().count());
        }
    }
}
