package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

class JDBCPreparedStatement
{
    private final Supplier<JDBCConnection> jdbcConnection;
    private final String sqlQuery;
    private final List<Object> arguments;

    private final PreparedStatement statement;

    JDBCPreparedStatement(Supplier<JDBCConnection> jdbcConnection, String sqlQuery, List<Object> arguments)
    {
        Objects.requireNonNull(jdbcConnection);
        Objects.requireNonNull(sqlQuery);
        Objects.requireNonNull(arguments);

        this.jdbcConnection = jdbcConnection;
        this.sqlQuery = sqlQuery;
        this.arguments = arguments;

        this.statement = createPreparedStatement();
    }

    private PreparedStatement createPreparedStatement()
    {
        try
        {
            var preparedStatement = jdbcConnection.get().prepareStatement(sqlQuery);

            for (int i = 0; i < arguments.size(); ++i)
            {
                preparedStatement.setObject(i+1, arguments.get(i));
            }

            return preparedStatement;
        } catch (SQLException e)
        {
            throw new IllegalArgumentException("Invalid Query " + sqlQuery + " " + e.getMessage(), e);
        }
    }

    PreparedStatement getStatement()
    {
        return statement;
    }
}
