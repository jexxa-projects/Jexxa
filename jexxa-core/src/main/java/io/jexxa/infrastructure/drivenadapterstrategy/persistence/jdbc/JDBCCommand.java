package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCCommand
{
    private final Supplier<JDBCConnection> jdbcConnection;
    private final String sqlCommand;

    public JDBCCommand(Supplier<JDBCConnection> jdbcConnection, String sqlCommand)
    {
        Objects.requireNonNull(jdbcConnection);
        Objects.requireNonNull(sqlCommand);

        this.jdbcConnection = jdbcConnection;
        this.sqlCommand = sqlCommand;
    }


    /**
     * Number of rows must not change
     *
     */
    public void asUpdate()
    {
        try (var statement = jdbcConnection.get().createStatement() )
        {
            if( statement.executeUpdate(sqlCommand) == 0)
            {
                throw new IllegalArgumentException("Command '" + sqlCommand + "'was executed but returned that nothing changed! ");
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
        Objects.requireNonNull(sqlCommand);

        try (var statement = jdbcConnection.get().createStatement() )
        {
            if( statement.executeUpdate(sqlCommand) == 1)
            {
                throw new IllegalArgumentException("Command '" + sqlCommand + "' was executed but returned that something changed! ");
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
        Objects.requireNonNull(sqlCommand);

        try (var statement = jdbcConnection.get().createStatement() )
        {
            statement.executeUpdate(sqlCommand);
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
