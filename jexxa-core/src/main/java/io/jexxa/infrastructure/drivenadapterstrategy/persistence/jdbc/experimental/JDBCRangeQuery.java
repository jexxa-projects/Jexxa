package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCRepository;

public class JDBCRangeQuery<T,S> implements IRangeQuery<T, S>
{
    private final JDBCRepository jdbcRepository;
    private final RangeComparator<T, S> rangeComparator;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();
    private final String nameOfRow;


    JDBCRangeQuery(JDBCRepository jdbcRepository, RangeComparator<T, S> rangeComparator, String nameOfRow, Class<T> aggregateClazz)
    {
        this.jdbcRepository = jdbcRepository;
        this.rangeComparator = rangeComparator;
        this.aggregateClazz = aggregateClazz;
        this.nameOfRow = nameOfRow;
    }

    @Override
    public List<T> getFrom(S startValue)
    {
        var sqlStartValue = rangeComparator.getIntValueS(startValue);

        var query = String.format(
                "select value from %s where %s >= %s",
                aggregateClazz.getSimpleName(),
                nameOfRow,
                sqlStartValue);

        return searchElements(query);
    }

    @Override
    public List<T> getRange(S startValue, S endValue)
    {
        var sqlStartValue = rangeComparator.getIntValueS(startValue);
        var sqlEndValue = rangeComparator.getIntValueS(endValue);


        var query = String.format(
                "select value from %s where %s >= %s AND %s <= %s",
                aggregateClazz.getSimpleName(),
                nameOfRow,
                sqlStartValue,
                nameOfRow,
                sqlEndValue
        );

        return searchElements(query);
    }

    @Override
    public List<T> getUntil(S endValue)
    {
        var sqlEndValue = rangeComparator.getIntValueS(endValue);

        var query = String.format(
                "select value from %s where %s <= %s",
                aggregateClazz.getSimpleName(),
                nameOfRow,
                sqlEndValue);

        return searchElements(query);
    }

    protected List<T> searchElements(String query)
    {
        return jdbcRepository
                .getConnection()
                .query(query)
                .asString()
                .map( element -> gson.fromJson(element, aggregateClazz))
                .collect(Collectors.toList());
    }
}
