package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc;

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
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IObjectStore;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.IObjectQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.comparator.MetadataComparator;
import io.jexxa.utils.JexxaLogger;
import io.jexxa.utils.json.JSONConverter;
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

        var iterator =comparatorFunctions.iterator();
        schemaKey =  iterator.next();
        schemaValue = iterator.next();

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


    public <S> IObjectQuery<T, S> getObjectQuery(M metadata)
    {
        if (!comparatorFunctions.contains(metadata))
        {
            throw new IllegalArgumentException("Unknown strategy for IRangedResult");
        }
        return new JDBCObjectQuery<>(this, metadata.getComparator(), metadata, aggregateClazz,comparatorSchema );
    }

    private static class JDBCObjectQuery<T,S, M extends Enum<M> & MetadataComparator> implements IObjectQuery<T, S>
    {
        private final JDBCRepository jdbcRepository;
        private final Comparator<T, S> comparator;

        private final Class<T> aggregateClazz;
        private final JSONConverter jsonConverter = getJSONConverter();
        private final M nameOfRow;
        private final M schemaValue;
        private final Class<M> comparatorSchema;


        public JDBCObjectQuery(JDBCRepository jdbcRepository, Comparator<T, S> comparator, M nameOfRow, Class<T> aggregateClazz, Class<M> comparatorSchema)
        {
            this.jdbcRepository = jdbcRepository;
            this.aggregateClazz = aggregateClazz;
            this.nameOfRow = nameOfRow;
            this.comparator = comparator;

            this.comparatorSchema = comparatorSchema;
            var comparatorFunctions = EnumSet.allOf(comparatorSchema);
            var iterator = comparatorFunctions.iterator();
            iterator.next();
            schemaValue = iterator.next();
        }

        @Override
        public List<T> getGreaterOrEqualThan(S startValue)
        {
            var sqlStartValue = comparator.convertValue(startValue);

            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .where(nameOfRow)
                    .isGreaterOrEqual(sqlStartValue)
                    .create();

            return searchElements(jdbcQuery);
        }

        @Override
        public List<T> getGreaterThan(S value)
        {
            var sqlStartValue = comparator.convertValue(value);

            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .where(nameOfRow)
                    .isGreaterThan(sqlStartValue)
                    .create();

            return searchElements(jdbcQuery);
        }

        @Override
        public List<T> getRangeClosed(S startValue, S endValue)
        {
            var sqlStartValue = comparator.convertValue(startValue);
            var sqlEndValue = comparator.convertValue(endValue);

            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .where(nameOfRow)
                    .isGreaterOrEqual(sqlStartValue)
                    .and(nameOfRow)
                    .isLessOrEqual(sqlEndValue)
                    .create();


            return searchElements(jdbcQuery);
        }

        @Override
        public List<T> getRange(S startValue, S endValue)
        {
            var sqlStartValue = comparator.convertValue(startValue);
            var sqlEndValue = comparator.convertValue(endValue);

            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .where(nameOfRow)
                    .isGreaterOrEqual(sqlStartValue)
                    .and(nameOfRow)
                    .isLessThan(sqlEndValue)
                    .create();


            return searchElements(jdbcQuery);
        }

        @Override
        public List<T> getLessOrEqualThan(S endValue)
        {
            var sqlEndValue = comparator.convertValue(endValue);

            //"select value from %s where %s <= %s",
            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .where(nameOfRow)
                    .isLessOrEqual(sqlEndValue)
                    .create();

            return searchElements(jdbcQuery);
        }

        @Override
        public List<T> getLessThan(S endValue)
        {
            var sqlEndValue = comparator.convertValue(endValue);

            //"select value from %s where %s <= %s",
            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .where(nameOfRow)
                    .isLessThan(sqlEndValue)
                    .create();

            return searchElements(jdbcQuery);
        }

        @Override
        public List<T> getAscending(int amount)
        {
            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .orderBy(nameOfRow, SQLOrder.ASC)
                    .limit(amount)
                    .create();

            return searchElements(jdbcQuery);
        }

        @Override
        public List<T> getDescending(int amount)
        {
            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .orderBy(nameOfRow, SQLOrder.DESC)
                    .limit(amount)
                    .create();

            return searchElements(jdbcQuery);
        }

        @Override
        public List<T> getEqualTo(S value)
        {
            var sqlValue = comparator.convertValue(value);
            var jdbcQuery = jdbcRepository.getConnection()
                    .createQuery(comparatorSchema)
                    .select( schemaValue )
                    .from(aggregateClazz)
                    .where(nameOfRow)
                    .isEqual(sqlValue)
                    .create();

            return searchElements(jdbcQuery);
        }

        protected List<T> searchElements(JDBCQuery query)
        {
            return query.asString()
                .flatMap(Optional::stream)
                .map( element -> jsonConverter.fromJson(element, aggregateClazz))
                .collect(Collectors.toList());
        }
    }
}
