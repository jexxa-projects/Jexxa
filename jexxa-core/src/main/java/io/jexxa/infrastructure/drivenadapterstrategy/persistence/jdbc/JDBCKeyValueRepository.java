package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.function.ThrowingConsumer;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class JDBCKeyValueRepository<T, K> implements IRepository<T, K>, AutoCloseable
{
    public static final String JDBC_URL = "io.jexxa.jdbc.url";
    public static final String JDBC_USERNAME = "io.jexxa.jdbc.username";
    public static final String JDBC_PASSWORD = "io.jexxa.jdbc.password";
    public static final String JDBC_DRIVER = "io.jexxa.jdbc.driver";
    public static final String JDBC_AUTOCREATE_TABLE = "io.jexxa.jdbc.autocreate.table";
    public static final String JDBC_AUTOCREATE_DATABASE = "io.jexxa.jdbc.autocreate.database";
    public static final String JDBC_MAX_RECONNECT = "io.jexxa.jdbc.max.reconnect";


    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCKeyValueRepository.class);
    private final int reconnectCount;

    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;
    private final JDBCConnection jdbcConnection;


    public JDBCKeyValueRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties)
    {
        this.keyFunction = keyFunction;
        this.aggregateClazz = aggregateClazz;

        this.jdbcConnection = new JDBCConnection(properties);
        this.reconnectCount = Integer.parseInt( properties.getProperty(JDBC_MAX_RECONNECT, "1") );

        autocreateTable(properties);
    }

    @Override
    public void update(T aggregate)
    {
        new Executor<Void>(jdbcConnection)
                .setMaxRetries(reconnectCount)
                .execute( this::internalUpdate, aggregate)
                .evaluateResult();
    }


    @Override
    public void remove(K key)
    {
        new Executor<Void>(jdbcConnection)
                .setMaxRetries(reconnectCount)
                .execute( this::internalRemove, key)
                .evaluateResult();
    }

    @Override
    public void removeAll()
    {
        new Executor<Void>(jdbcConnection)
                .setMaxRetries(reconnectCount)
                .execute( this::internalRemoveAll )
                .evaluateResult();
    }

    @Override
    public void add(T aggregate)
    {
        new Executor<Void>(jdbcConnection)
                .setMaxRetries(reconnectCount)
                .execute( this::internalAdd, aggregate)
                .evaluateResult();
    }

    @Override
    public Optional<T> get(K primaryKey)
    {
        return new Executor<Optional<T>>(jdbcConnection)
                .setMaxRetries(reconnectCount)
                .execute( this::internalGet, primaryKey)
                .evaluateResult();
    }


    @Override
    public List<T> get()
    {
        return new Executor<List<T>>(jdbcConnection)
                .setMaxRetries(reconnectCount)
                .execute(this::internalGetAll)
                .evaluateResult();
    }

    public void internalRemove(K key)
    {
        Validate.notNull(key);

        Gson gson = new Gson();
        String jsonKey = gson.toJson(key);

        try (var preparedStatement = jdbcConnection.prepareStatement("delete from " + aggregateClazz.getSimpleName() + " where key= ?"))
        {
            preparedStatement.setString(1, jsonKey);

            if ( preparedStatement.executeUpdate() == 0 )
            {
                throw new IllegalArgumentException("Could not delete aggregate " + aggregateClazz.getSimpleName());
            }
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }

    }

    public void internalRemoveAll()
    {

        try ( var statement = jdbcConnection.prepareStatement("delete from " + aggregateClazz.getSimpleName()))
        {
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }

    }

    @SuppressWarnings("DuplicatedCode")
    public void internalAdd(T aggregate)
    {
        Validate.notNull(aggregate);

        Gson gson = new Gson();
        String key = gson.toJson(keyFunction.apply(aggregate));
        String value = gson.toJson(aggregate);

        try (var preparedStatement = jdbcConnection.prepareStatement("insert into " + aggregate.getClass().getSimpleName()+ " values(?,?)"))
        {
            preparedStatement.setString(1, key);
            preparedStatement.setString(2, value);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }

    }

    @SuppressWarnings({"DuplicatedCode", "unused"})
    public void internalUpdate(T aggregate)
    {
        Validate.notNull(aggregate);

        Gson gson = new Gson();
        String key = gson.toJson(keyFunction.apply(aggregate));
        String value = gson.toJson(aggregate);

        try (var preparedStatement = jdbcConnection.prepareStatement("update " + aggregateClazz.getSimpleName() + " set value = ? where key = ?") )
        {
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, key);
            int result = preparedStatement.executeUpdate();
            if (result == 0)
            {
                throw new IllegalArgumentException("Could not update aggregate " + aggregate.getClass().getSimpleName());
            }
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }

    }


    public Optional<T> internalGet(K primaryKey)
    {
        Validate.notNull(primaryKey);

        Gson gson = new Gson();
        String key = gson.toJson(primaryKey);

        try ( var preparedStatement = jdbcConnection.prepareStatement("select value from " + aggregateClazz.getSimpleName() + " where key = ? ")  )
        {
            preparedStatement.setString(1, key);
            try ( var resultSet = preparedStatement.executeQuery() )
            {
                if ( resultSet.next() )
                {
                    return Optional.ofNullable(gson.fromJson(resultSet.getString(1), aggregateClazz));
                }
                else
                {
                    return Optional.empty();
                }
            }

        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public List<T> internalGetAll()
    {
        var result = new ArrayList<T>();
        Gson gson = new Gson();
        try (
                var statement = jdbcConnection.createStatement();
                var resultSet = statement.executeQuery("select value from "+ aggregateClazz.getSimpleName())
             )
        {
            while (resultSet.next())
            {
                T aggregate = gson.fromJson( resultSet.getString(1), aggregateClazz);
                result.add(aggregate);
            }
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }

        return result;
    }


    private void autocreateTable(final Properties properties)
    {
        if (properties.containsKey(JDBC_AUTOCREATE_TABLE))
        {
            try (
                 Statement statement = jdbcConnection.createStatement())
            {
                var command = String.format("CREATE TABLE IF NOT EXISTS %s ( key VARCHAR %s PRIMARY KEY, value text) "
                        , aggregateClazz.getSimpleName()
                        , getMaxVarChar(properties.getProperty(JDBC_URL))
                );
                statement.executeUpdate(command);
            }
            catch (SQLException e)
            {
                LOGGER.warn("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
            }
        }
    }


    public void close()
    {
        Optional.ofNullable(jdbcConnection)
                .ifPresent(ThrowingConsumer.exceptionLogger(JDBCConnection::close));
    }

    private static String getMaxVarChar(String jdbcDriver)
    {
        if ( jdbcDriver.toLowerCase().contains("oracle") )
        {
            return "(4000)";
        }

        if ( jdbcDriver.toLowerCase().contains("postgres") )
        {
            return ""; // Note in general Postgres does not have a real upper limit.
        }

        if ( jdbcDriver.toLowerCase().contains("h2") )
        {
            return "(" + Integer.MAX_VALUE + ")";
        }

        if ( jdbcDriver.toLowerCase().contains("mysql") )
        {
            return "(65535)";
        }

        return "(255)";
    }





    JDBCConnection getJdbcConnection()
    {
        return jdbcConnection;
    }


    public static class Executor<R>
    {
        private final JDBCConnection jdbcConnection;
        private R returnValue;
        private RuntimeException firstException;
        private int currentCounter = 0;
        private int maxRetries = 1;
        private boolean operationSuccess = false;

        public Executor(JDBCConnection jdbcConnection)
        {
            this.jdbcConnection = jdbcConnection;
        }

        Executor<R> setMaxRetries(int maxRetries)
        {
            this.maxRetries = maxRetries;
            return this;
        }

        Executor<R> execute( Supplier<R> function )
        {
            for (currentCounter = 0; currentCounter <= maxRetries; ++currentCounter)
            {
                try {
                    returnValue = function.get();
                    operationSuccess = true;
                    break;
                } catch (RuntimeException e)
                {
                    validateJDBCConnection(e) ;
                }
            }
            return this;
        }

        <X> Executor<R> execute( Function<X, R> function, X parameter)
        {
            for (currentCounter = 0; currentCounter <= maxRetries; ++currentCounter)
            {
                try {
                    returnValue = function.apply(parameter);
                    operationSuccess = true;
                    break;
                } catch (RuntimeException e)
                {
                    validateJDBCConnection(e) ;
                }
            }
            return this;
        }

        <T> Executor<R> execute( Consumer<T> function, T parameter)
        {
            for (currentCounter = 0; currentCounter <= maxRetries; ++currentCounter)
            {
                try {
                    function.accept(parameter);
                    operationSuccess = true;
                    break;
                } catch (RuntimeException e)
                {
                    validateJDBCConnection(e) ;
                }
            }
            return this;
        }

        Executor<R> execute( Runnable function)
        {
            for (currentCounter = 0; currentCounter <= maxRetries; ++currentCounter)
            {
                try {
                    function.run();
                    operationSuccess = true;
                    break;
                } catch (RuntimeException e)
                {
                    validateJDBCConnection(e) ;
                }
            }
            return this;
        }

        public R evaluateResult()
        {
            if ( operationSuccess )
            {
                if (firstException != null) {
                    LOGGER.warn("Connection reset successful! Query successfully executed with {}th retry ... ", currentCounter);
                }

                return returnValue;
            }

            throw new IllegalStateException("JDBC connection is invalid. Connection failed with: " + firstException.getMessage()) ;
        }

        void validateJDBCConnection(RuntimeException e)
        {
            if (jdbcConnection.isValid())
            {
                throw e;
            }

            LOGGER.warn("JDBC connection is invalid. Reason: {}", e.getMessage());
            LOGGER.warn("Rest connection and retry query... ");
            jdbcConnection.reset();

            if ( this.firstException == null)
            {
                this.firstException = e;
            }
        }

    }

}
