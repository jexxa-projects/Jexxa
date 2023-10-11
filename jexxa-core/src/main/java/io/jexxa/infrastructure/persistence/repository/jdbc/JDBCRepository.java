package io.jexxa.infrastructure.persistence.repository.jdbc;

import io.jexxa.adapterapi.invocation.transaction.TransactionHandler;
import io.jexxa.adapterapi.invocation.transaction.TransactionManager;
import io.jexxa.common.wrapper.jdbc.JDBCConnection;
import io.jexxa.common.wrapper.jdbc.JDBCConnectionPool;

import java.util.Objects;
import java.util.Properties;

import static io.jexxa.common.wrapper.logger.SLF4jLogger.getLogger;

public abstract class JDBCRepository implements TransactionHandler {
    private final Properties properties;

    protected JDBCRepository(Properties properties)
    {
        this.properties = Objects.requireNonNull(properties);
        getConnection(); // To ensure that connection is valid
        TransactionManager.registerTransactionHandler(this);
    }

    /**
     * Returns a JDBCConnection that is in a valid state. If the connection can not be changed into a valid state, an IllegalStateException is thrown.
     *
     * @throws IllegalStateException if JDBCConnection can not be reset
     * @return JDBCConnection that is in a valid state.
     */
    public JDBCConnection getConnection()
    {
        return JDBCConnectionPool.getConnection(properties, this);
    }
    @Override
    public void initTransaction()
    {
        getConnection().disableAutoCommit();
    }
    @Override
    public void closeTransaction()
    {
        getConnection().commit();
        getConnection().enableAutoCommit();
    }

    @Override
    public void rollback()
    {
        try {
            getConnection().rollback();
        } catch (IllegalStateException e)
        {
            getLogger(getClass()).error("An exception occurred during rollback. Reason: {}", e.getMessage());
        }
    }
}
