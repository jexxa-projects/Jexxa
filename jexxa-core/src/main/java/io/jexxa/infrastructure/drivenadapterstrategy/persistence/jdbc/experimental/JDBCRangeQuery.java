package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCQuery;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCRepository;

public class JDBCRangeQuery<T,S, M extends Enum<M> & SearchStrategy> implements IRangeQuery<T, S>
{
    private final JDBCRepository jdbcRepository;
    private final RangeComparator<T, S> rangeComparator;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();
    private final M nameOfRow;
    private final M schemaValue;
    private final Class<M> comparatorSchema;


    JDBCRangeQuery(JDBCRepository jdbcRepository, RangeComparator<T, S> rangeComparator, M nameOfRow, Class<T> aggregateClazz, Class<M> comparatorSchema)
    {
        this.jdbcRepository = jdbcRepository;
        this.rangeComparator = rangeComparator;
        this.aggregateClazz = aggregateClazz;
        this.nameOfRow = nameOfRow;
        this.comparatorSchema = comparatorSchema;
        var comparatorFunctions = EnumSet.allOf(comparatorSchema);
        var iterator = comparatorFunctions.iterator();
        iterator.next();
        schemaValue = iterator.next();
    }

    @Override
    public List<T> getFrom(S startValue)
    {
        var sqlStartValue = rangeComparator.getIntValueS(startValue);

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
        var sqlStartValue = rangeComparator.getIntValueS(startValue);
        var sqlEndValue = rangeComparator.getIntValueS(endValue);

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
        var sqlEndValue = rangeComparator.getIntValueS(endValue);

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

    protected List<T> searchElements(JDBCQuery query)
    {
        return query.asString()
            .flatMap(Optional::stream)
            .map( element -> gson.fromJson(element, aggregateClazz))
            .collect(Collectors.toList());
    }
}
