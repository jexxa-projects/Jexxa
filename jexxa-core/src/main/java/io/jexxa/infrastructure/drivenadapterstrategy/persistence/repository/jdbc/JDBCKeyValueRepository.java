package io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.jdbc;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCObject;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database.DatabaseManager;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.database.IDatabase;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.IRepository;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.json.JSONManager;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCTableBuilder.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.JSONB;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.jdbc.JDBCKeyValueRepository.KeyValueSchema.KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.repository.jdbc.JDBCKeyValueRepository.KeyValueSchema.VALUE;
import static io.jexxa.utils.json.JSONManager.getJSONConverter;

public class JDBCKeyValueRepository<T, K> extends JDBCRepository implements IRepository<T, K>
{
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCKeyValueRepository.class);

    private final Function<T,K> keyFunction;
    private final Class<T> aggregateClazz;
    private final IDatabase database;

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
        this.database = DatabaseManager.getDatabase(properties);

        manageDBTable(properties);
    }

    protected JDBCKeyValueRepository(Class<T> aggregateClazz, Function<T,K> keyFunction, Properties properties, boolean manageTable)
    {
        super(properties);

        this.keyFunction = Objects.requireNonNull( keyFunction );
        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.database = DatabaseManager.getDatabase(properties);

        if ( manageTable )
        {
            manageDBTable(properties);
        }
    }


    @Override
    public void remove(K key)
    {
        Objects.requireNonNull(key);
        var jdbcKey = new JDBCObject(getJSONConverter().toJson(key), database.matchDataType(JSONB));

        var command = getConnection().createCommand(KeyValueSchema.class)
                .deleteFrom(aggregateClazz)
                .where(KEY)
                .isEqual(jdbcKey)
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
                .values(new JDBCObject[]{
                        primaryKeyToJSONB(keyFunction.apply(aggregate)),
                        valueToJSONB(aggregate)}
                )
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
                .set(VALUE, valueToJSONB(aggregate))
                .where(KEY)
                .isEqual(primaryKeyToJSONB(keyFunction.apply(aggregate)))
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
                .isEqual(primaryKeyToJSONB(primaryKey))
                .create();

        return  query
                .asString()
                .flatMap(Optional::stream)
                .findFirst()
                .map( element -> {System.out.println("bla _ "  + element); return getJSONConverter().fromJson(element, aggregateClazz);})
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
                .map( element -> {System.out.println(element);return getJSONConverter().fromJson(element, aggregateClazz);})
                .collect(Collectors.toList());
    }

    private void manageDBTable(Properties properties)
    {
        if (properties.containsKey(JDBCConnection.JDBC_AUTOCREATE_TABLE))
        {
            autocreateTableKeyValue();
            alterKeyValueRows();
        }
    }

    private void autocreateTableKeyValue()
    {
        try{

            var command = getConnection().createTableCommand(KeyValueSchema.class)
                    .createTableIfNotExists(aggregateClazz)
                    .addColumn(KEY, database.matchPrimaryKey(JSONB))
                    .addConstraint(PRIMARY_KEY)
                    .addColumn(VALUE, database.matchDataType(JSONB))
                    .create();

            command.asIgnore();
        }
        catch (RuntimeException e)
        {
            LOGGER.warn("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
        }
    }

    protected void alterKeyValueRows()
    {
        var keyRow = getConnection().createTableCommand(KeyValueSchema.class)
                .alterTable(aggregateClazz)
                .alterColumn(KEY, database.alterPrimaryKeyTo(JSONB), database.alterColumnUsingStatement(KEY, JSONB))
                .create();

        keyRow.asIgnore();

        var valueRow = getConnection().createTableCommand(KeyValueSchema.class)
                .alterTable(aggregateClazz)
                .alterColumn(VALUE, database.alterDataTypeTo(JSONB), database.alterColumnUsingStatement(KEY, JSONB))
                .create();

        valueRow.asIgnore();

    }


    protected JDBCObject primaryKeyToJSONB(Object value)
    {
        return new JDBCObject(JSONManager.getJSONConverter().toJson(value), database.matchPrimaryKey(JSONB));
    }

    protected JDBCObject valueToJSONB(Object value)
    {
        return new JDBCObject(JSONManager.getJSONConverter().toJson(value), database.matchDataType(JSONB));
    }
}
