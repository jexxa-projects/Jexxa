package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class JDBCCommand extends JDBCPreparedStatement
{
    private static final String SQL_STATEMENT_FAILED = "Could not execute SQL Statement : ";
    /**
     * Creates a JDBCCommand
     *
     * @param jdbcConnection used connection to execute command
     * @param sqlCommand mmust include the complete command with all attributes included. Note: The sqlCommand can include a
     *                   '?' as placeholder for arguments
     * @param arguments includes all arguments of the sqlCommand
     */
    public JDBCCommand(Supplier<JDBCConnection> jdbcConnection, String sqlCommand, List<Object> arguments)
    {
        super(jdbcConnection, sqlCommand, arguments);
    }


    /**
     * Execute command as 'update' so that number of rows must not change
     *
     */
    public void asUpdate()
    {
        try (var preparedStatement = createPreparedStatement())
        {
            if( preparedStatement.executeUpdate() == 0)
            {
                throw new IllegalArgumentException("Command was executed but returned that nothing changed! ");
            }
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(SQL_STATEMENT_FAILED + getSQLStatement(), e);
        }
    }

    /**
     * Execute command as 'empty' so that number of rows must change
     */
    public void asEmpty( )
    {
        try (var preparedStatement = createPreparedStatement())
        {
            if( preparedStatement.executeUpdate() == 1)
            {
                throw new IllegalArgumentException("Command was executed but returned that something changed! ");
            }
        }

        catch (SQLException e)
        {
            throw new IllegalArgumentException(SQL_STATEMENT_FAILED + getSQLStatement(), e);
        }
    }

    /**
     * Just execute command. Return value of command is not processed
     */
    public void asIgnore( )
    {
        try (var preparedStatement = createPreparedStatement())
        {
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(SQL_STATEMENT_FAILED + getSQLStatement(), e);
        }
    }
}
