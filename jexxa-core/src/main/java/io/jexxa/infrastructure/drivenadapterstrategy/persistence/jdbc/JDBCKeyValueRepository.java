package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.utils.JexxaLogger;
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
    private final Gson gson = new Gson();


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
        Objects.requireNonNull(key);

        String command = String.format("delete from %s where key= '%s'"
                , getAggregateName()
                , gson.toJson(key));

        createCommand()
                .execute(command)
                .asUpdate();
    }

    @Override
    public void removeAll()
    {
        String command = String.format("delete from %s", getAggregateName());

        createCommand()
                .execute(command)
                .asIgnore();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void add(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        String command = String.format("insert into %s values( '%s' , '%s' )"
                , getAggregateName()
                , gson.toJson(keyFunction.apply(aggregate))
                , gson.toJson(aggregate));

        createCommand()
                .execute(command)
                .asUpdate();
    }

    @SuppressWarnings({"DuplicatedCode", "unused"})
    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        String command = String.format("update %s set value = '%s' where key = '%s'"
                , getAggregateName()
                , gson.toJson(aggregate)
                , gson.toJson(keyFunction.apply(aggregate)));

        createCommand()
                .execute(command)
                .asUpdate();
    }

    @Override
    public Optional<T> get(K primaryKey)
    {
        Objects.requireNonNull(primaryKey);

        String query = String.format( "select value from %s where key = '%s'"
                , getAggregateName()
                , gson.toJson(primaryKey));

        return createQuery()
                .query(query)
                .asString()
                .findFirst()
                .map( element -> gson.fromJson(element, aggregateClazz))
                .or(Optional::empty);
    }

    @Override
    public List<T> get()
    {
        return createQuery()
                .query("select value from "+ getAggregateName())
                .asString()
                .map( element -> gson.fromJson(element, aggregateClazz))
                .collect(Collectors.toList());
    }


    private void autocreateTable(final Properties properties)
    {
        if (properties.containsKey(JDBC_AUTOCREATE_TABLE))
        {
            try{
                var command = String.format("CREATE TABLE IF NOT EXISTS %s ( key VARCHAR %s PRIMARY KEY, value text) "
                        , aggregateClazz.getSimpleName()
                        , getMaxVarChar(properties.getProperty(JDBC_URL)));

                createCommand()
                        .execute(command)
                        .asIgnore();
            }
            catch (RuntimeException e)
            {
                LOGGER.warn("Could not create table {} => Assume that table already exists", getAggregateName());
            }
        }
    }

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
