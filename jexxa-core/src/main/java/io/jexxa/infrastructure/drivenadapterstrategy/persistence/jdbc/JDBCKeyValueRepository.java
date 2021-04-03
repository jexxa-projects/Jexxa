package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository.KeyValueSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository.KeyValueSchema.VALUE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCTableBuilder.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.TEXT;
import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;

public class JDBCKeyValueRepository<T, K> extends JDBCRepository implements IRepository<T, K>
{
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
        Objects.requireNonNull(key);

        var command = getConnection().createCommand(KeyValueSchema.class)
                .deleteFrom(aggregateClazz)
                .where(KEY)
                .isEqual(getJSONConverter().toJson(key))
                .create();

        command.asUpdate();
    }

    @Override
    public void removeAll()
    {
        var command = getConnection().createCommand(KeyValueSchema.class)
                .deleteFrom(aggregateClazz)
                .create();

        command.asIgnore();
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void add(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var command = getConnection().createCommand(KeyValueSchema.class)
                .insertInto(aggregateClazz)
                .values(getJSONConverter().toJson(keyFunction.apply(aggregate)), getJSONConverter().toJson(aggregate))
                .create();

        command.asUpdate();
    }

    @SuppressWarnings({"DuplicatedCode", "unused"})
    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var command = getConnection().createCommand(KeyValueSchema.class)
                .update(aggregateClazz)
                .set(VALUE, getJSONConverter().toJson(aggregate) )
                .where(KEY)
                .isEqual(getJSONConverter().toJson(keyFunction.apply(aggregate)))
                .create();

        command.asUpdate();
    }

    @Override
    public Optional<T> get(K primaryKey)
    {
        Objects.requireNonNull(primaryKey);

        var query = getConnection().createQuery(KeyValueSchema.class)
                .select(VALUE)
                .from(aggregateClazz)
                .where(KEY)
                .isEqual(getJSONConverter().toJson(primaryKey))
                .create();

        return  query
                .asString()
                .flatMap(Optional::stream)
                .findFirst()
                .map( element -> getJSONConverter().fromJson(element, aggregateClazz))
                .or(Optional::empty);
    }

    @Override
    public List<T> get()
    {
        var query = getConnection().createQuery(KeyValueSchema.class)
                .select(VALUE)
                .from(aggregateClazz)
                .create();

        return query
                .asString()
                .flatMap(Optional::stream)
                .map( element -> getJSONConverter().fromJson(element, aggregateClazz))
                .collect(Collectors.toList());
    }


    private void autocreateTable(final Properties properties)
    {
        if (properties.containsKey(JDBCConnection.JDBC_AUTOCREATE_TABLE))
        {
            try{

                var command = getConnection().createTableCommand(KeyValueSchema.class)
                        .createTableIfNotExists(aggregateClazz)
                        .addColumn(KEY, getMaxVarChar(properties.getProperty(JDBCConnection.JDBC_URL)))
                        .addConstraint(PRIMARY_KEY)
                        .addColumn(VALUE, TEXT)
                        .create();

                command.asIgnore();
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

    private static SQLDataType getMaxVarChar(String jdbcDriver)
    {
        if ( jdbcDriver.toLowerCase().contains("oracle") )
        {
            return SQLDataType.VARCHAR(4000);
        }

        if ( jdbcDriver.toLowerCase().contains("postgres") )
        {
            return SQLDataType.VARCHAR; // Note in general Postgres does not have a real upper limit.
        }

        if ( jdbcDriver.toLowerCase().contains("h2") )
        {
            return SQLDataType.VARCHAR(Integer.MAX_VALUE);
        }

        if ( jdbcDriver.toLowerCase().contains("mysql") )
        {
            return SQLDataType.VARCHAR(65535);
        }

        return SQLDataType.VARCHAR(255);
    }


    enum KeyValueSchema
    {
        KEY,
        VALUE
    }
}
