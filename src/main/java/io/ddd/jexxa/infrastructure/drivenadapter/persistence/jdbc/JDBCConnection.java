package io.ddd.jexxa.infrastructure.drivenadapter.persistence.jdbc;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import com.google.gson.Gson;
import io.ddd.jexxa.infrastructure.drivenadapter.persistence.IRepositoryConnection;
import io.ddd.jexxa.utils.JexxaLogger;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;

public class JDBCConnection<T, K> implements IRepositoryConnection<T, K>, AutoCloseable
{
    public static final String JDBC_URL = "io.ddd.jexxa.jdbc.url";
    public static final String JDBC_USERNAME = "io.ddd.jexxa.jdbc.username";
    public static final String JDBC_PASSWORD = "io.ddd.jexxa.jdbc.password";
    public static final String JDBC_DRIVER = "io.ddd.jexxa.jdbc.driver";
    public static final String JDBC_AUTOCREATE = "io.ddd.jexxa.jdbc.autocreate";
    public static final String JDBC_DEFAULT_URL = "io.ddd.jexxa.jdbc.defaulturl";



    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCConnection.class);

    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;
    private Connection connection;


    public JDBCConnection(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties)
    {
        this.keyFunction = keyFunction;
        this.aggregateClazz = aggregateClazz;

        initDBDriver(properties);
        if (properties.containsKey(JDBC_AUTOCREATE)) {
            if (properties.containsKey(JDBC_DEFAULT_URL)) {
                createDatabase(properties);
                createTable(properties);
            }
            else
            {
                LOGGER.warn("Key {} is defined but key {} ist not set => Ignore {} ", JDBC_AUTOCREATE, JDBC_DEFAULT_URL, JDBC_AUTOCREATE);
            }

        }

        this.connection = initJDBCConnection(properties);
    }


    @Override
    public void remove(K key)
    {
        Validate.notNull(key);

        Gson gson = new Gson();
        String jsonKey = gson.toJson(key);

        try (Statement statement = connection.createStatement())
        {
            var command = String.format("delete from %s where key='%s'", aggregateClazz.getSimpleName(),  jsonKey);

            if ( statement.executeUpdate(command) == 0 ){
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

        try (Statement statement = connection.createStatement())
        {
            var command = "delete from " + aggregateClazz.getSimpleName();
            statement.executeUpdate(command);
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

        StringReader keyReader = new StringReader(key);
        StringReader valueReader = new StringReader(value);

        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into " + aggregate.getClass().getSimpleName() + " values(?,?)"))
        {
            preparedStatement.setCharacterStream(1, keyReader, key.length());
            preparedStatement.setCharacterStream(2, valueReader, value.length());
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

        StringReader keyReader = new StringReader(key);
        StringReader valueReader = new StringReader(value);

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "update " + aggregate.getClass().getSimpleName() + " set value = ? where key = ?")
        )
        {
            preparedStatement.setCharacterStream(1, valueReader, value.length());
            preparedStatement.setCharacterStream(2, keyReader, key.length());
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
        String query = String.format("select value from %s where key='%s'", aggregateClazz.getSimpleName(), key);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)
        )
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
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select value from "+ aggregateClazz.getSimpleName())
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
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void createDatabase(final Properties properties)
    {
        var splittedURL = properties.getProperty(JDBC_URL).split("/");
        var dbName = splittedURL[splittedURL.length-1].toLowerCase(); //last part of the URL is the name of the database (Note: Some DBs such as postgres require a name in lower case!)

        Properties creationProperties = new Properties();
        creationProperties.putAll(properties);

        try (var setupConnection = DriverManager.
                getConnection(
                        creationProperties.getProperty(JDBC_DEFAULT_URL),
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

    private void createTable(final Properties properties)
    {
        try (var setupConnection = DriverManager.
                getConnection(
                        properties.getProperty(JDBC_URL).toLowerCase(),
                        properties.getProperty(JDBC_USERNAME),
                        properties.getProperty(JDBC_PASSWORD));
             Statement statement = setupConnection.createStatement())
        {
            var command = String.format("CREATE TABLE %s ( key VARCHAR(255) PRIMARY KEY, value text) ", aggregateClazz.getSimpleName());
            statement.executeUpdate(command);
        }
        catch (SQLException e)
        {
            LOGGER.warn("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
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
            throw new IllegalArgumentException("Specified JDBC driver is not available: " + properties.getProperty(JDBC_DRIVER));
        }

    }

    public void close()
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
            connection = null;
        }
        catch (SQLException e)
        {
            LOGGER.error(e.getMessage());
        }
    }
}
