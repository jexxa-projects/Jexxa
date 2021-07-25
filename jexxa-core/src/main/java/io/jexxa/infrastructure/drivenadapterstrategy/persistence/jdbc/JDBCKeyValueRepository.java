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
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;

public class JDBCKeyValueRepository<T, K> extends JDBCRepository implements IRepository<T, K>
{
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCKeyValueRepository.class);

    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;

    public enum KeyValueSchema
    {
        KEY,
        VALUE
    }

    public JDBCKeyValueRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties)
    {
        super(properties);

        this.keyFunction = Objects.requireNonNull( keyFunction );
        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);

        autocreateTableKeyValue(properties);
    }

    protected JDBCKeyValueRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties, boolean autoCreateTable)
    {
        super(properties);

        this.keyFunction = Objects.requireNonNull( keyFunction );
        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);

        if ( autoCreateTable )
        {
            autocreateTableKeyValue(properties);
        }
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


    private void autocreateTableKeyValue(Properties properties)
    {
        Objects.requireNonNull(properties);
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
                LOGGER.warn("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
            }
        }
    }

}
