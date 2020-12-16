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

    JDBCRepository(Properties properties)
    {
        Objects.requireNonNull(properties);

        this.jdbcConnection = new JDBCConnection(properties);
    }

    public void close()
    {
        ThrowingConsumer.exceptionLogger(JDBCConnection::close);
    }

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

    protected abstract String getAggregateName();

}
