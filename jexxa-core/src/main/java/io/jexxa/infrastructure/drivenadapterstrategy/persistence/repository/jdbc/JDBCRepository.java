package io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.jdbc;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.utils.function.ThrowingConsumer;

import java.util.Objects;
import java.util.Properties;

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
    public final JDBCConnection getConnection()
    {
        return jdbcConnection.validateConnection();
    }

}
