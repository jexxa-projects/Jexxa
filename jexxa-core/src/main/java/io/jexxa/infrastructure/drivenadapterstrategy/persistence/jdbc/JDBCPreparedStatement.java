package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

class JDBCPreparedStatement
{
    private final Supplier<JDBCConnection> jdbcConnection;
    private final String sqlStatement;
    private final List<Object> arguments;

    private final PreparedStatement statement;

    JDBCPreparedStatement(Supplier<JDBCConnection> jdbcConnection, String sqlStatement, List<Object> arguments)
    {
        this.jdbcConnection = Objects.requireNonNull(jdbcConnection);
        this.sqlStatement = Objects.requireNonNull(sqlStatement);
        this.arguments = Objects.requireNonNull(arguments);

        this.statement = createPreparedStatement();
    }

    private PreparedStatement createPreparedStatement()
    {
        try
        {
            var preparedStatement = jdbcConnection.get().prepareStatement(sqlStatement);

            for (int i = 0; i < arguments.size(); ++i)
            {
                preparedStatement.setObject(i+1, arguments.get(i));
            }

            return preparedStatement;
        } catch (SQLException e)
        {
            throw new IllegalArgumentException("Invalid Query " + sqlStatement + " " + e.getMessage(), e);
        }
    }

    public PreparedStatement getStatement()
    {
        return statement;
    }

    protected String getSQLStatement()
    {
        return sqlStatement;
    }
}
