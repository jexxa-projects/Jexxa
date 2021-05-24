package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.util.Objects;
import java.util.Properties;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;
import io.jexxa.utils.function.ThrowingConsumer;

public abstract class JDBCRepository  implements AutoCloseable
{
    private final JDBCConnection jdbcConnection;

    protected JDBCRepository(Properties properties)
    {
        Objects.requireNonNull(properties);

        this.jdbcConnection = new JDBCConnection(properties);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void close()
    {
        ThrowingConsumer.exceptionLogger(JDBCConnection::close);
    }

    /**
     * Returns a JDBCConnection that is in a valid state. If the connection can not be changed into a valid state, an IllegalStateException is thrown.
     *
     * @throws IllegalStateException if JDBCConnection can not be reset
     * @return JDBCConnection that is in a valid state.
     */
    @SuppressWarnings("java:S2139")  // To explicitly show log messages in backend. Otherwise they are forwarded to the client and not visible in backend
    public final JDBCConnection getConnection()
    {
        return jdbcConnection.validateConnection();
    }

    protected static SQLDataType getMaxVarChar(String jdbcDriver)
    {
        if ( jdbcDriver.toLowerCase().contains("oracle") )
        {
            return SQLDataType.VARCHAR(4000);
        }

        if ( jdbcDriver.toLowerCase().contains("postgres") )
        {
            return SQLDataType.VARCHAR; // Note in general Postgres does not have a real upper limit.
        }

        if ( jdbcDriver.toLowerCase().contains("h2") )
        {
            return SQLDataType.VARCHAR(Integer.MAX_VALUE);
        }

        if ( jdbcDriver.toLowerCase().contains("mysql") )
        {
            return SQLDataType.VARCHAR(65535);
        }

        return SQLDataType.VARCHAR(255);
    }

}
