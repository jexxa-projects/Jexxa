package io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.jdbc;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnectionPool;

import java.util.Objects;
import java.util.Properties;

public abstract class JDBCRepository
{
    private final Properties properties;

    protected JDBCRepository(Properties properties)
    {
        this.properties = Objects.requireNonNull(properties);
        getConnection(); // To ensure that connection is valid
    }

    /**
     * Returns a JDBCConnection that is in a valid state. If the connection can not be changed into a valid state, an IllegalStateException is thrown.
     *
     * @throws IllegalStateException if JDBCConnection can not be reset
     * @return JDBCConnection that is in a valid state.
     */
    public final JDBCConnection getConnection()
    {
        return JDBCConnectionPool.getConnection(properties);
    }

}
