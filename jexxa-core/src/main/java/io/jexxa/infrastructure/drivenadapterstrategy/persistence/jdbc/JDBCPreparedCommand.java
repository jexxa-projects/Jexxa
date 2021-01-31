package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCPreparedCommand extends JDBCPreparedStatement
{
    JDBCPreparedCommand(Supplier<JDBCConnection> jdbcConnection, String sqlQuery, List<Object> arguments)
    {
        super(jdbcConnection, sqlQuery, arguments);
    }


    /**
     * Number of rows must not change
     *
     */
    public void asUpdate()
    {
        try
        {
            if( getStatement().executeUpdate() == 0)
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
            if( getStatement().executeUpdate() == 1)
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
            getStatement().executeUpdate();
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
