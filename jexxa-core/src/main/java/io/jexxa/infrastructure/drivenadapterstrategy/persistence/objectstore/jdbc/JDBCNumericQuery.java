package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.jdbc;

import static io.jexxa.utils.json.JSONManager.getJSONConverter;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.MetadataComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.NumericComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCConnection;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;
import io.jexxa.utils.json.JSONConverter;

class JDBCNumericQuery<T,S, M extends Enum<M> & MetadataComparator> implements INumericQuery<T, S>
{
    private final Supplier<JDBCConnection> jdbcConnection;
    private final NumericComparator<T, S> numericComparator;

    private final Class<T> aggregateClazz;
    private final JSONConverter jsonConverter = getJSONConverter();
    private final M nameOfRow;
    private final M schemaValue;
    private final Class<M> comparatorSchema;


    JDBCNumericQuery(Supplier<JDBCConnection> jdbcConnection,
                     NumericComparator<T, S> numericComparator,
                     M nameOfRow,
                     Class<T> aggregateClazz,
                     Class<M> comparatorSchema)
    {
        this.jdbcConnection = jdbcConnection;
        this.aggregateClazz = aggregateClazz;
        this.nameOfRow = nameOfRow;
        this.numericComparator = numericComparator;

        this.comparatorSchema = comparatorSchema;
        var comparatorFunctions = EnumSet.allOf(comparatorSchema);
        var iterator = comparatorFunctions.iterator();
        iterator.next();
        schemaValue = iterator.next();
    }

    @Override
    public List<T> isGreaterOrEqualThan(S startValue)
    {
        var sqlStartValue = numericComparator.convertValue(startValue);

        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( schemaValue )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isGreaterOrEqual(sqlStartValue)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isGreaterThan(S value)
    {
        var sqlStartValue = numericComparator.convertValue(value);

        var jdbcQuery = jdbcConnection.get()
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
        var sqlStartValue = numericComparator.convertValue(startValue);
        var sqlEndValue = numericComparator.convertValue(endValue);

        var jdbcQuery = jdbcConnection.get()
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
        var sqlStartValue = numericComparator.convertValue(startValue);
        var sqlEndValue = numericComparator.convertValue(endValue);

        var jdbcQuery = jdbcConnection.get()
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
    public List<T> isLessOrEqualThan(S endValue)
    {
        var sqlEndValue = numericComparator.convertValue(endValue);

        //"select value from %s where %s <= %s",
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( schemaValue )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isLessOrEqual(sqlEndValue)
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
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( schemaValue )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.ASC)
                .limit(amount)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getAscending()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( schemaValue )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.ASC)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getDescending(int amount)
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( schemaValue )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.DESC)
                .limit(amount)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> getDescending()
    {
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( schemaValue )
                .from(aggregateClazz)
                .orderBy(nameOfRow, SQLOrder.DESC)
                .create();

        return searchElements(jdbcQuery);
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        var sqlValue = numericComparator.convertValue(value);
        var jdbcQuery = jdbcConnection.get()
                .createQuery(comparatorSchema)
                .select( schemaValue )
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
                .select( schemaValue )
                .from(aggregateClazz)
                .where(nameOfRow)
                .isNotEqual(sqlValue)
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

