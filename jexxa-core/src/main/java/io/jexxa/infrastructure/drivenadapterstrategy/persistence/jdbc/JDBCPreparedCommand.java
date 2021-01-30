package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@SuppressWarnings("unused")
public class JDBCPreparedCommand
{
    private final PreparedStatement statement;

    JDBCPreparedCommand(PreparedStatement statement)
    {
        Objects.requireNonNull(statement);

        this.statement = statement;
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
}
