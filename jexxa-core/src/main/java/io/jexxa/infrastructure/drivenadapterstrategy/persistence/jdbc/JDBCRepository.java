package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.util.Objects;
import java.util.Properties;

import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;
import org.slf4j.Logger;

public abstract class JDBCRepository  implements AutoCloseable
{
    private final JDBCConnection jdbcConnection;
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCRepository.class);

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
    protected final JDBCConnection getConnection()
    {
        try
        {
            if (!jdbcConnection.isValid())
            {
                LOGGER.warn("JDBC connection for Aggregate {} is invalid. ", getAggregateName());
                LOGGER.warn("Try to reset JDBC connection for Aggregate {}", getAggregateName());
                jdbcConnection.reset();
                LOGGER.warn("JDBC connection for Aggregate {} successfully restarted.", getAggregateName());
            }
        } catch (RuntimeException e)
        {
            LOGGER.error("Could not reset JDBC connection for Aggregate {}. Reason: {}", getAggregateName(), e.getMessage());
            throw e;
        }

        return jdbcConnection;
    }

    public JDBCCommand createCommand()
    {
        return new JDBCCommand(this::getConnection);
    }

    public JDBCQuery createQuery()
    {
        return new JDBCQuery(this::getConnection);
    }

    protected abstract String getAggregateName();

}
