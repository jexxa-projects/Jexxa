package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.Comparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import io.jexxa.utils.json.JSONConverter;

class JDBCNumericQuery<T,S, M extends Enum<M> & MetadataComparator> implements INumericQuery<T, S>
{
    private final Supplier<JDBCConnection> jdbcConnection;
    private final Comparator<T, S, ? extends Number> numericComparator;

    private final Class<T> aggregateClazz;
    private final JSONConverter jsonConverter = getJSONConverter();
    private final M nameOfRow;
    private final Class<M> comparatorSchema;

    @SuppressWarnings("unused") //Type required for java type inference
    private final Class<S> queryType;


    JDBCNumericQuery(Supplier<JDBCConnection> jdbcConnection,
                     Comparator<T, S, ? extends Number> numericComparator,
                     M nameOfRow,
                     Class<T> aggregateClazz,
                     Class<M> comparatorSchema,
                     Class<S> queryType)
    {
        this.jdbcConnection = Objects.requireNonNull( jdbcConnection );
        this.aggregateClazz = Objects.requireNonNull(aggregateClazz);
        this.nameOfRow = Objects.requireNonNull(nameOfRow);
        this.numericComparator = Objects.requireNonNull(numericComparator);
        this.comparatorSchema = Objects.requireNonNull(comparatorSchema);
        this.queryType = Objects.requireNonNull(queryType);
    }

    @Override
    public List<T> isGreaterOrEqualThan(S startValue)
    {
        var sqlStartValue = numericComparator.convertValue(startValue);

        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterOrEqual(sqlStartValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isGreaterThan(S value)
    {
        var sqlStartValue = numericComparator.convertValue(value);

        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterThan(sqlStartValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getRangeClosed(S startValue, S endValue)
    {
        var sqlStartValue = numericComparator.convertValue(startValue);
        var sqlEndValue = numericComparator.convertValue(endValue);

        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterOrEqual(sqlStartValue)
                .and(nameOfRow)
                .isLessOrEqual(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();


        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getRange(S startValue, S endValue)
    {
        var sqlStartValue = numericComparator.convertValue(startValue);
        var sqlEndValue = numericComparator.convertValue(endValue);

        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterOrEqual(sqlStartValue)
                .and(nameOfRow)
                .isLessThan(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();


        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isLessOrEqualThan(S endValue)
    {
        var sqlEndValue = numericComparator.convertValue(endValue);

        //"select value from %s where %s <= %s",
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isLessOrEqual(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isLessThan(S endValue)
    {
        var sqlEndValue = numericComparator.convertValue(endValue);

        //"select value from %s where %s <= %s",
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isLessThan(sqlEndValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getAscending(int amount)
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .limit(amount)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getAscending()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getDescending(int amount)
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.DESC_NULLS_LAST)
                .limit(amount)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getDescending()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.DESC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        var sqlValue = numericComparator.convertValue(value);
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isEqual(sqlValue)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isNotEqualTo(S value)
    {
        var sqlValue = numericComparator.convertValue(value);
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isNotEqual(sqlValue)
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isNull()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isNull()
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isNotNull()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( JDBCObjectStore.KeyValueSchema.class, JDBCObjectStore.KeyValueSchema.VALUE )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isNotNull()
                .orderBy(nameOfRow, SQLOrder.ASC_NULLS_LAST)
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

