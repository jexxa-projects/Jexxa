package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCRepository;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.builder.SQLOrder;
import io.jexxa.utils.json.JSONConverter;
import io.jexxa.utils.json.JSONManager;

public class JDBCSubset<T,S, M extends Enum<M> & ComparatorSchema> implements ISubset<T, S>
{
    private final JDBCRepository jdbcRepository;
    private final Comparator<T, S> comparator;

    private final Class<T> aggregateClazz;
    private final JSONConverter jsonConverter = JSONManager.getJSONConverter();
    private final M nameOfRow;
    private final M schemaValue;
    private final Class<M> comparatorSchema;


    public JDBCSubset(JDBCRepository jdbcRepository, Comparator<T, S> comparator, M nameOfRow, Class<T> aggregateClazz, Class<M> comparatorSchema)
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
    public List<T> getFrom(S startValue)
    {
        var sqlStartValue = comparator.convert(startValue);

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
    public List<T> getRange(S startValue, S endValue)
    {
        var sqlStartValue = comparator.convert(startValue);
        var sqlEndValue = comparator.convert(endValue);

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
    public List<T> getUntil(S endValue)
    {
        var sqlEndValue = comparator.convert(endValue);

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
    public List<T> get(S value)
    {
        var sqlValue = comparator.convert(value);
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
