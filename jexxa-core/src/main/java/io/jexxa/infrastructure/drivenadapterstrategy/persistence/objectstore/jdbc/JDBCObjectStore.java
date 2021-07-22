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
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.NumericComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.IStringQuery;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.json.JSONManager;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;



@SuppressWarnings("unused")
public class JDBCObjectStore<T,K, M extends Enum<M> & MetadataComparator> extends JDBCRepository implements IObjectStore<T, K, M>
{
    private static final Logger LOGGER = JexxaLogger.getLogger(JDBCObjectStore.class);
    private static final String SQL_NUMERIC = "NUMERIC";

    private final Function<T, K> keyFunction;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();

    private final Class<M> comparatorSchema;
    private final Set<M> comparatorFunctions;
    private final M schemaKey;
    private final M schemaValue;


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
        schemaKey =  iterator.next();
        schemaValue = iterator.next();

        //TODO: This must be changed, because it is only valid in case the JDBCObjectStore creates the table by itself
        Validate.isTrue( "KEY".equals(schemaKey.name()), "First entry of ComparatorSchema must be 'KEY' ");
        Validate.isTrue( "VALUE".equals(schemaValue.name()), "Second entry of ComparatorSchema must be 'VALUE' ");

        autocreateTable(properties);
    }


    @Override
    public void update(T aggregate)
    {
        Objects.requireNonNull(aggregate);

        var keySet =comparatorFunctions
                .stream()
                .skip(1)
                .toArray();

        var comparatorValueSet = comparatorFunctions
                .stream()
                .skip(2) // Skip the key and value
                .map( element -> element.getComparator().convertAggregate(aggregate) )
                .collect(Collectors.toList());

        var valueSet = new ArrayList<>();
        valueSet.add(JSONManager.getJSONConverter().toJson(aggregate));
        valueSet.addAll(comparatorValueSet);

        // Skip the key
        //"update %s where key = '%s' "  ComparatorValues... " '%s' = '%s' "

        var command = getConnection()
                .createCommand(comparatorSchema)
                .update(aggregateClazz)
                .set(keySet, valueSet.toArray() )
                .where(schemaKey).isEqual(JSONManager.getJSONConverter().toJson( keyFunction.apply(aggregate) ))
                .create();

        command.asUpdate();
    }

    @Override
    public void remove(K key)
    {
        Objects.requireNonNull(key);

        var command = getConnection()
                .createCommand(comparatorSchema)
                .deleteFrom(aggregateClazz)
                .where(this.schemaKey)
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
        var jsonConverter = JSONManager.getJSONConverter();

        var keyValue = Stream.of(
                jsonConverter.toJson(keyFunction.apply(aggregate)),
                jsonConverter.toJson(aggregate));

        var remainingData =  comparatorFunctions.stream().skip(2).map(element -> element.getComparator().convertAggregate(aggregate));

        var command = getConnection()
                .createCommand(comparatorSchema)
                .insertInto(aggregateClazz)
                .values(Streams.concat(keyValue, remainingData).toArray())
                .create();

        command.asUpdate();
    }



    @Override
    public List<T> get()
    {
        var query = getConnection().createQuery(comparatorSchema)
                .select(schemaValue)
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

        var query = getConnection().createQuery(comparatorSchema)
                .select(schemaValue)
                .from(aggregateClazz)
                .where(schemaKey)
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
                        .addColumn(schemaKey, getMaxVarChar(properties.getProperty(JDBCConnection.JDBC_URL)))
                        .addConstraint(PRIMARY_KEY)
                        .addColumn(schemaValue, TEXT);

                comparatorFunctions.stream().skip(2).forEach( element -> command.addColumn(element, NUMERIC));

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
        //noinspection unchecked
        NumericComparator<T, S> numberComparator = (NumericComparator) metadata.getComparator();

        return new JDBCNumericQuery<>(this::getConnection, numberComparator, metadata, aggregateClazz,comparatorSchema, queryType );
    }

    @Override
    public <S> IStringQuery<T, S> getStringQuery(M metadata, Class<S> queryType)
    {
        return null;
    }
}
