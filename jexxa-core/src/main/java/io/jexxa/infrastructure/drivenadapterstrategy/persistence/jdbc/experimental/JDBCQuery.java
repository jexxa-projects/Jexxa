package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.utils.json.JSONConverter;
import io.jexxa.utils.json.JSONManager;

public class JDBCQuery<T,S, M extends Enum<M> & MetadataComparator> implements IQuery<T, S>
{
    private final JDBCRepository jdbcRepository;
    private final Comparator<T, S> comparator;

    private final Class<T> aggregateClazz;
    private final JSONConverter jsonConverter = JSONManager.getJSONConverter();
    private final M nameOfRow;
    private final M schemaValue;
    private final Class<M> comparatorSchema;


    public JDBCQuery(JDBCRepository jdbcRepository, Comparator<T, S> comparator, M nameOfRow, Class<T> aggregateClazz, Class<M> comparatorSchema)
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

    protected List<T> searchElements(io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery query)
    {
        return query.asString()
            .flatMap(Optional::stream)
            .map( element -> jsonConverter.fromJson(element, aggregateClazz))
            .collect(Collectors.toList());
    }
}
