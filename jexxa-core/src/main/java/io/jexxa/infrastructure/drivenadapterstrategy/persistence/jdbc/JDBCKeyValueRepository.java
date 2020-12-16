package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.utils.JexxaLogger;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class JDBCKeyValueRepository<T, K> extends JDBCRepository implements IRepository<T, K>
{
    public static final String JDBC_URL = "io.jexxa.jdbc.url";
    public static final String JDBC_USERNAME = "io.jexxa.jdbc.username";
    public static final String JDBC_PASSWORD = "io.jexxa.jdbc.password";
    public static final String JDBC_DRIVER = "io.jexxa.jdbc.driver";
    public static final String JDBC_AUTOCREATE_TABLE = "io.jexxa.jdbc.autocreate.table";
    public static final String JDBC_AUTOCREATE_DATABASE = "io.jexxa.jdbc.autocreate.database";

    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCKeyValueRepository.class);

    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;


    public JDBCKeyValueRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties)
    {
        super(properties);

        this.keyFunction = keyFunction;
        this.aggregateClazz = aggregateClazz;

        autocreateTable(properties);
    }


    @Override
    public void remove(K key)
    {
        Validate.notNull(key);

        Gson gson = new Gson();
        String jsonKey = gson.toJson(key);

        try (var preparedStatement = getConnection().prepareStatement("delete from " + getAggregateName() + " where key= ?"))
        {
            preparedStatement.setString(1, jsonKey);

            if ( preparedStatement.executeUpdate() == 0 )
            {
                throw new IllegalArgumentException("Could not delete aggregate " + getAggregateName());
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

        try ( var statement = getConnection().prepareStatement("delete from " + getAggregateName()))
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

        try (var preparedStatement = getConnection().prepareStatement("insert into " + getAggregateName() + " values(?,?)"))
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

        try (var preparedStatement = getConnection().prepareStatement("update " + getAggregateName() + " set value = ? where key = ?") )
        {
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, key);
            int result = preparedStatement.executeUpdate();
            if (result == 0)
            {
                throw new IllegalArgumentException("Could not update aggregate " + getAggregateName());
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

        try ( var preparedStatement = getConnection().prepareStatement("select value from " + getAggregateName() + " where key = ? ")  )
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
                var statement = getConnection().createStatement();
                var resultSet = statement.executeQuery("select value from "+ getAggregateName())
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
                 Statement statement = getConnection().createStatement())
            {
                var command = String.format("CREATE TABLE IF NOT EXISTS %s ( key VARCHAR %s PRIMARY KEY, value text) "
                        , aggregateClazz.getSimpleName()
                        , getMaxVarChar(properties.getProperty(JDBC_URL))
                );
                statement.executeUpdate(command);
            }
            catch (SQLException e)
            {
                LOGGER.warn("Could not create table {} => Assume that table already exists", getAggregateName());
            }
        }
    }

    @Override
    protected String getAggregateName()
    {
        return aggregateClazz.getSimpleName();
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

}
