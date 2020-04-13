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

/**
 * TODO: Create/Validate expected tables in database
 *  String sql = "CREATE TABLE <tablename>" +
 *                    " key VARCHAR(255) PRIMARY KEY, " + // Alternatively <key.simpleName> ?!
 *                    " value text)";
 * @param <T>
 * @param <K>
 */
public class JDBCConnection<T, K> implements IRepositoryConnection<T, K>, AutoCloseable
{
    public static final String JDBC_URL = "io.ddd.jexxa.jdbc.url";
    public static final String JDBC_USERNAME = "io.ddd.jexxa.jdbc.username";
    public static final String JDBC_PASSWORD = "io.ddd.jexxa.jdbc.password";
    public static final String JDBC_DRIVER = "io.ddd.jexxa.jdbc.driver";

    private static final Logger logger = JexxaLogger.getLogger(JDBCConnection.class);

    private final Function<T,K> keyFunction;
    private final Properties properties;
    private final Class<T> aggregateClazz;
    private Connection connection;



    public JDBCConnection(Class<T> aggregateClazz, Class<K> keyClazz, Function<T,K> keyFunction, Properties properties)
    {
        this.keyFunction = keyFunction;
        this.properties = properties;
        this.aggregateClazz = aggregateClazz;
        initJDBCConnection();
    }

    
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
                throw new IllegalStateException("Could not update aggregate " + aggregate.getClass().getSimpleName());
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
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
                throw new IllegalStateException("Could not delete aggregate " + aggregateClazz.getSimpleName());
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }

    }

    @Override
    public void removeAll()
    {

        try (Statement statement = connection.createStatement())
        {
            var command = "delete from " + aggregateClazz.getSimpleName();

            if ( statement.executeUpdate(command) == 0 ){
                throw new IllegalStateException("Could not delete aggregate " + aggregateClazz.getSimpleName());
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }

    }

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
        catch (Exception e)
        {
            throw new IllegalStateException(e);
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
           resultSet.next();
           return Optional.ofNullable(gson.fromJson(resultSet.getString(1), aggregateClazz));
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public List<T> get()
    {
        var result = new ArrayList<T>();
        Gson gson = new Gson();
        try (
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("select daten from "+ aggregateClazz.getSimpleName())
             )
        {
            while (resultSet.next())
            {
                T aggregate = gson.fromJson( resultSet.getString(1), aggregateClazz);
                result.add(aggregate);
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }

        return result;
    }

    public void initJDBCConnection()
    {
        try
        {
            Class.forName(properties.getProperty(JDBC_DRIVER));
            connection = DriverManager.
                    getConnection(
                            properties.getProperty(JDBC_URL),
                            properties.getProperty(JDBC_USERNAME),
                            properties.getProperty(JDBC_PASSWORD)
                    );

            connection.setAutoCommit(true);

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
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
            logger.error(e.getMessage());
        }
    }
}
