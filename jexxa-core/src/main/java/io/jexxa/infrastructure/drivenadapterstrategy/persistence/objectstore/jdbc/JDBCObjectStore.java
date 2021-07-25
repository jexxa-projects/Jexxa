package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.JDBCTableBuilder.SQLConstraint.PRIMARY_KEY;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.NUMERIC;
import static io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLDataType.TEXT;
import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import io.jexxa.utils.JexxaLogger;
import org.slf4j.Logger;


@SuppressWarnings("unused")
public class JDBCObjectStore<T,K, M extends Enum<M> & MetadataComparator> extends JDBCRepository implements IObjectStore<T, K, M>
{
    enum KeyValueSchema
    {
        KEY,
        VALUE
    }

    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCObjectStore.class);
    private static final String SQL_NUMERIC = "NUMERIC";

    private final Function<T, K> keyFunction;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();

    private final Class<M> comparatorSchema;
    private final Set<M> comparatorFunctions;

    public JDBCObjectStore(
            Class<T> aggregateClazz,
            Function<T, K> keyFunction,
            Class<M> comparatorSchema,
            Properties properties
    )
    {
        super(properties);
        this.keyFunction = keyFunction;
        this.aggregateClazz = aggregateClazz;
        this.comparatorSchema = comparatorSchema;
        this.comparatorFunctions = EnumSet.allOf(comparatorSchema);

        var iterator = comparatorFunctions.iterator();

        autocreateTable(properties);
    }


    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var valueSet = new ArrayList<>();
        List<String> keySet = new ArrayList<>();

        valueSet.add(getJSONConverter().toJson(aggregate));
        comparatorFunctions.forEach( element -> valueSet.add(element.getComparator().convertAggregate(aggregate)) );

        keySet.add(KeyValueSchema.VALUE.name());
        comparatorFunctions.forEach(element -> keySet.add(element.name()));

        //"update %s where key = '%s' "  ComparatorValues... " '%s' = '%s' "
        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .update(aggregateClazz)
                .set(keySet.toArray(new String[0]), valueSet.toArray() )
                .where(KeyValueSchema.KEY).isEqual(getJSONConverter().toJson( keyFunction.apply(aggregate) ))
                .create();

        command.asUpdate();
    }

    @Override
    public void remove(K key)
    {
        Objects.requireNonNull(key);

        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .deleteFrom(aggregateClazz)
                .where(KeyValueSchema.KEY)
                .isEqual(getJSONConverter().toJson(key))
                .create();

        command.asUpdate();
    }

    @Override
    public void removeAll()
    {
        var command = getConnection()
                .createCommand(comparatorSchema)
                .deleteFrom(aggregateClazz)
                .create();

        command.asIgnore();
    }

    @Override
    public void add(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var jsonConverter = getJSONConverter();

        var objectList = new ArrayList<>();
        objectList.add (jsonConverter.toJson(keyFunction.apply(aggregate)));
        objectList.add (jsonConverter.toJson(aggregate));
        comparatorFunctions.forEach(metadata -> objectList.add(metadata.getComparator().convertAggregate(aggregate)));

        var command = getConnection()
                .createCommand(KeyValueSchema.class)
                .insertInto(aggregateClazz)
                .values(objectList.toArray())
                .create();

        command.asUpdate();
    }



    @Override
    public List<T> get()
    {
        var query = getConnection().createQuery(KeyValueSchema.class)
                .select(KeyValueSchema.VALUE)
                .from(aggregateClazz)
                .create();

        return query
                .asString()
                .flatMap(Optional::stream)
                .map( element -> getJSONConverter().fromJson(element, aggregateClazz))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<T> get(K primaryKey)
    {
        Objects.requireNonNull(primaryKey);

        var query = getConnection().createQuery(KeyValueSchema.class)
                .select(KeyValueSchema.VALUE)
                .from(aggregateClazz)
                .where(KeyValueSchema.KEY)
                .isEqual(getJSONConverter().toJson(primaryKey))
                .create();

        return query
                .asString()
                .flatMap(Optional::stream)
                .findFirst()
                .map(element -> getJSONConverter().fromJson(element, aggregateClazz))
                .or(Optional::empty);
    }

    private void autocreateTable(Properties properties)
    {
        Objects.requireNonNull(properties);
        if (properties.containsKey(JDBCConnection.JDBC_AUTOCREATE_TABLE))
        {
            try{

                var command = getConnection().createTableCommand(comparatorSchema)
                        .createTableIfNotExists(aggregateClazz)
                        .addColumn(KeyValueSchema.KEY, getMaxVarChar(properties.getProperty(JDBCConnection.JDBC_URL)), KeyValueSchema.class)
                        .addConstraint(PRIMARY_KEY)
                        .addColumn(KeyValueSchema.VALUE, TEXT, KeyValueSchema.class);

                comparatorFunctions.forEach(element ->
                {
                    if ( Number.class.isAssignableFrom(element.getComparator().getValueType()) )
                    {
                        command.addColumn(element, NUMERIC);
                    } else if (String.class.isAssignableFrom(element.getComparator().getValueType())){
                        command.addColumn(element, TEXT);
                    } else {
                        throw new IllegalArgumentException("Unsupported Value type " + element.getComparator().getValueType().getName() +
                                ". Supported Value types are subtypes of Number and String ");
                    }
                });

                command.create().asIgnore();

            }
            catch (RuntimeException e)
            {
                LOGGER.warn("Could not create table {} => Assume that table already exists", aggregateClazz.getSimpleName());
            }
        }
    }

    public <S> INumericQuery<T, S> getNumericQuery(M metadata, Class<S> queryType)
    {
        if (!comparatorFunctions.contains(metadata))
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        return new JDBCNumericQuery<>(this::getConnection, metadata.getComparator(), metadata, aggregateClazz,comparatorSchema, queryType );
    }

    @Override
    public <S> IStringQuery<T, S> getStringQuery(M metadata, Class<S> queryType)
    {
        if (!comparatorFunctions.contains(metadata))
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }

        return new JDBCStringQuery<>(this::getConnection, metadata.getComparator(), metadata, aggregateClazz,comparatorSchema, queryType );
    }
}
