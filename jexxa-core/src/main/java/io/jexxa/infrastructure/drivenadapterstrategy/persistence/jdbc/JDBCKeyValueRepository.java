package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository.KeyValueSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCKeyValueRepository.KeyValueSchema.VALUE;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.SQLSyntax.SQLDataType.TEXT;

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
    /**
     * @deprecated use constant {@link JDBCConnection#JDBC_URL} instead
     */
    @Deprecated(forRemoval = true)
    public static final String JDBC_URL = "io.jexxa.jdbc.url";

    /**
     * @deprecated use constant {@link JDBCConnection#JDBC_USERNAME} instead
     */
    @Deprecated(forRemoval = true)
    public static final String JDBC_USERNAME = "io.jexxa.jdbc.username";

    /**
     * @deprecated use constant {@link JDBCConnection#JDBC_PASSWORD} instead
     */
    @Deprecated(forRemoval = true)
    public static final String JDBC_PASSWORD = "io.jexxa.jdbc.password";

    /**
     * @deprecated use constant {@link JDBCConnection#JDBC_DRIVER} instead
     */
    @Deprecated(forRemoval = true)
    public static final String JDBC_DRIVER = "io.jexxa.jdbc.driver";

    /**
     * @deprecated use constant {@link JDBCConnection#JDBC_AUTOCREATE_TABLE} instead
     */
    @Deprecated(forRemoval = true)
    public static final String JDBC_AUTOCREATE_TABLE = "io.jexxa.jdbc.autocreate.table";

    /**
     * @deprecated use constant {@link JDBCConnection#JDBC_AUTOCREATE_DATABASE} instead
     */
    @Deprecated(forRemoval = true)
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

        var command = getConnection().createCommand(KeyValueSchema.class)
                .deleteFrom(aggregateClazz)
                .where(KEY)
                .isEqual(gson.toJson(key))
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
                .values(gson.toJson(keyFunction.apply(aggregate)), gson.toJson(aggregate))
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
                .set(VALUE,gson.toJson(aggregate) )
                .where(KEY)
                .isEqual(gson.toJson(keyFunction.apply(aggregate)))
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
                .isEqual(gson.toJson(primaryKey))
                .create();

        return  query
                .asString()
                .flatMap(Optional::stream)
                .findFirst()
                .map( element -> gson.fromJson(element, aggregateClazz))
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
                .map( element -> gson.fromJson(element, aggregateClazz))
                .collect(Collectors.toList());
    }


    private void autocreateTable(final Properties properties)
    {
        if (properties.containsKey(JDBCConnection.JDBC_AUTOCREATE_TABLE))
        {
            try{

                var command = getConnection().createCommand(KeyValueSchema.class)
                        .createTableIfNotExists(aggregateClazz)
                        .addColumn(KEY, getMaxVarChar(properties.getProperty(JDBCConnection.JDBC_URL)))
                        .addConstraint(SQLSyntax.SQLConstraint.PRIMARY_KEY)
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

    private static SQLSyntax.SQLDataType getMaxVarChar(String jdbcDriver)
    {
        if ( jdbcDriver.toLowerCase().contains("oracle") )
        {
            return SQLSyntax.SQLDataType.VARCHAR(4000);
        }

        if ( jdbcDriver.toLowerCase().contains("postgres") )
        {
            return SQLSyntax.SQLDataType.VARCHAR; // Note in general Postgres does not have a real upper limit.
        }

        if ( jdbcDriver.toLowerCase().contains("h2") )
        {
            return SQLSyntax.SQLDataType.VARCHAR(Integer.MAX_VALUE);
        }

        if ( jdbcDriver.toLowerCase().contains("mysql") )
        {
            return SQLSyntax.SQLDataType.VARCHAR(65535);
        }

        return SQLSyntax.SQLDataType.VARCHAR(255);
    }


    enum KeyValueSchema
    {
        KEY,
        VALUE
    }
}
