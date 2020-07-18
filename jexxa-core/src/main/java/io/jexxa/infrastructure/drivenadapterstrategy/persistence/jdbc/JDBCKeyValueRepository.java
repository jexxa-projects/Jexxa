package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.ThrowingConsumer;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class JDBCKeyValueRepository<T, K> implements IRepository<T, K>, AutoCloseable
{
    public static final String JDBC_URL = "io.jexxa.jdbc.url";
    public static final String JDBC_USERNAME = "io.jexxa.jdbc.username";
    public static final String JDBC_PASSWORD = "io.jexxa.jdbc.password";
    public static final String JDBC_DRIVER = "io.jexxa.jdbc.driver";
    public static final String JDBC_AUTOCREATE_DATABASE = "io.jexxa.jdbc.autocreate.database";
    public static final String JDBC_AUTOCREATE_TABLE = "io.jexxa.jdbc.autocreate.table";



    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCKeyValueRepository.class);

    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;
    private Connection connection;


    public JDBCKeyValueRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties)
    {
        this.keyFunction = keyFunction;
        this.aggregateClazz = aggregateClazz;

        validateProperties(properties);

        initDBDriver(properties);

        autocreateDatabase(properties);

        autocreateTable(properties);

        this.connection = initJDBCConnection(properties);
    }


    @Override
    public void remove(K key)
    {
        Validate.notNull(key);

        Gson gson = new Gson();
        String jsonKey = gson.toJson(key);

        try (var preparedStatement = connection.prepareStatement("delete from " + aggregateClazz.getSimpleName() + " where key= ?"))
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

    @Override
    public void removeAll()
    {

        try ( var statement = connection.prepareStatement("delete from " + aggregateClazz.getSimpleName()))
        {
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new IllegalArgumentException(e);
        }

    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void add(T aggregate)
    {
        Validate.notNull(aggregate);

        Gson gson = new Gson();
        String key = gson.toJson(keyFunction.apply(aggregate));
        String value = gson.toJson(aggregate);

        try (var preparedStatement = connection.prepareStatement("insert into " + aggregate.getClass().getSimpleName()+ " values(?,?)"))
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
    @Override
    public void update(T aggregate)
    {
        Validate.notNull(aggregate);

        Gson gson = new Gson();
        String key = gson.toJson(keyFunction.apply(aggregate));
        String value = gson.toJson(aggregate);

        try (var preparedStatement = connection.prepareStatement("update " + aggregateClazz.getSimpleName() + " set value = ? where key = ?") )
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



    @Override
    public Optional<T> get(K primaryKey)
    {
        Validate.notNull(primaryKey);

        Gson gson = new Gson();
        String key = gson.toJson(primaryKey);

        try ( var preparedStatement = connection.prepareStatement("select value from " + aggregateClazz.getSimpleName() + " where key = ? ")  )
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


    @Override
    public List<T> get()
    {
        var result = new ArrayList<T>();
        Gson gson = new Gson();
        try (
                var statement = connection.createStatement();
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

    private Connection initJDBCConnection(final Properties properties)
    {

        try {
            connection = DriverManager.getConnection(
                          properties.getProperty(JDBC_URL),
                          properties.getProperty(JDBC_USERNAME),
                          properties.getProperty(JDBC_PASSWORD)
            );

            connection.setAutoCommit(true);
            return connection;
        }
        catch (SQLException e)
        {                              
            throw new IllegalArgumentException(e);
        }
    }

    private void autocreateDatabase(final Properties properties)
    {
        if (properties.containsKey(JDBC_AUTOCREATE_DATABASE))
        {
            var splitURL = properties.getProperty(JDBC_URL).split("/");
            var dbName = splitURL[splitURL.length - 1].toLowerCase(Locale.ENGLISH); //last part of the URL is the name of the database (Note: Some DBs such as postgres require a name in lower case!)

            Properties creationProperties = new Properties();
            creationProperties.putAll(properties);

            try (var setupConnection = DriverManager.
                    getConnection(
                            creationProperties.getProperty(JDBC_AUTOCREATE_DATABASE),
                            creationProperties.getProperty(JDBC_USERNAME),
                            creationProperties.getProperty(JDBC_PASSWORD));
                 Statement statement = setupConnection.createStatement())
            {
                setupConnection.setAutoCommit(true);
                statement.execute(String.format("create DATABASE %s ", dbName));
                LOGGER.info("Database {} successfully created ", dbName);
            }
            catch (SQLException e)
            {
                LOGGER.warn("Could not create database {} => Assume that database already exists", dbName);
            }
        }
    }

    private void autocreateTable(final Properties properties)
    {
        if (properties.containsKey(JDBC_AUTOCREATE_TABLE))
        {
            try (var setupConnection = DriverManager.
                    getConnection(
                            properties.getProperty(JDBC_URL).toLowerCase(Locale.ENGLISH),
                            properties.getProperty(JDBC_USERNAME),
                            properties.getProperty(JDBC_PASSWORD));
                 Statement statement = setupConnection.createStatement())
            {
                var command = String.format("CREATE TABLE IF NOT EXISTS %s ( key VARCHAR(255) PRIMARY KEY, value text) ", aggregateClazz.getSimpleName());
                statement.executeUpdate(command);
            }
            catch (SQLException e)
            {
                LOGGER.warn("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
            }
        }
    }

    private void initDBDriver(Properties properties)
    {
        try
        {
            Class.forName(properties.getProperty(JDBC_DRIVER));
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalArgumentException("Specified JDBC driver is not available: " + properties.getProperty(JDBC_DRIVER), e);
        }

    }

    private void validateProperties(Properties properties)
    {
        Validate.isTrue(properties.containsKey(JDBC_URL), "Parameter " + JDBC_URL + " is missing");
        Validate.isTrue(properties.containsKey(JDBC_DRIVER), "Parameter " + JDBC_DRIVER + " is missing");
    }

    public void close()
    {
        Optional.ofNullable(connection)
                .ifPresent(ThrowingConsumer.exceptionLogger(Connection::close));
    }
}
