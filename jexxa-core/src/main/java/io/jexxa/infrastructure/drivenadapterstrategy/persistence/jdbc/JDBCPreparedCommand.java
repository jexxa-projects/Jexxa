package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCPreparedCommand
{
    private final Supplier<JDBCConnection> jdbcConnection;
    private final String sqlQuery;
    private final List<Object> arguments;

    private final PreparedStatement statement;


    JDBCPreparedCommand(Supplier<JDBCConnection> jdbcConnection, String sqlQuery, List<Object> arguments)
    {
        Objects.requireNonNull(jdbcConnection);
        Objects.requireNonNull(sqlQuery);
        Objects.requireNonNull(arguments);

        this.jdbcConnection = jdbcConnection;
        this.sqlQuery = sqlQuery;
        this.arguments = arguments;

        this.statement = createPreparedStatement();
    }


    /**
     * Number of rows must not change
     *
     */
    public void asUpdate()
    {
        try
        {
            if( statement.executeUpdate() == 0)
            {
                throw new IllegalArgumentException("Command was executed but returned that nothing changed! ");
            }
        }

        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Number of rows must change
     *
     */
    public void asEmpty( )
    {
        try
        {
            if( statement.executeUpdate() == 1)
            {
                throw new IllegalArgumentException("Command was executed but returned that something changed! ");
            }
        }

        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Just execute command. Return value of command is not processed
     */
    public void asIgnore( )
    {
        try
        {
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
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
}
