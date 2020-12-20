package io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.experimental;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.jdbc.JDBCRepository;

public class JDBCRangedResult<T,S> implements IRangedResult<T, S>
{
    private final JDBCRepository jdbcRepository;
    private final Comparator<T, S> comparator;
    private final Class<T> aggregateClazz;
    private final Gson gson = new Gson();
    private final String nameOfRow;


    JDBCRangedResult(JDBCRepository jdbcRepository, Comparator<T, S> comparator, String nameOfRow, Class<T> aggregateClazz)
    {
        this.jdbcRepository = jdbcRepository;
        this.comparator = comparator;
        this.aggregateClazz = aggregateClazz;
        this.nameOfRow = nameOfRow;
    }

    @Override
    public List<T> getFrom(S startValue)
    {
        var sqlStartValue = comparator.getIntValueS(startValue);

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
        var sqlStartValue = comparator.getIntValueS(startValue);
        var sqlEndValue = comparator.getIntValueS(endValue);


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
        var sqlEndValue = comparator.getIntValueS(endValue);

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
                .createQuery()
                .query(query)
                .asString()
                .map( element -> gson.fromJson(element, aggregateClazz))
                .collect(Collectors.toList());
    }
}
