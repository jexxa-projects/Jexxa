package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCCommand
{
    private final Supplier<JDBCConnection> jdbcConnection;
    private String command;

    public JDBCCommand(Supplier<JDBCConnection> jdbcConnection)
    {
        this.jdbcConnection = jdbcConnection;
    }

    public JDBCCommand execute(String command)
    {
        this.command = command;
        return this;
    }

    /**
     * Number of rows must not change
     *
     */
    public void asUpdate()
    {
        Objects.requireNonNull(command);

        try (var statement = jdbcConnection.get().createStatement() )
        {
            if( statement.executeUpdate(command) == 0)
            {
                throw new IllegalArgumentException("Command '" + command + "'was executed but returned that nothing changed! ");
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
        Objects.requireNonNull(command);

        try (var statement = jdbcConnection.get().createStatement() )
        {
            if( statement.executeUpdate(command) == 1)
            {
                throw new IllegalArgumentException("Command '" + command + "' was executed but returned that something changed! ");
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
        Objects.requireNonNull(command);

        try (var statement = jdbcConnection.get().createStatement() )
        {
            statement.executeUpdate(command);
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
