package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.NumericComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;

class IMDBNumericQuery<T, K, S> implements INumericQuery<T, S>
{
    private final NumericComparator<T, S> numericComparator;
    private final Map<K, T> internalMap;

    @SuppressWarnings("unused") //Type required for java type inference
    private final Class<S> queryType;

    private Map<K, T> getOwnAggregateMap()
    {
        return internalMap;
    }

    IMDBNumericQuery(Map<K, T> internalMap, NumericComparator<T, S> numericComparator, Class<S> queryType)
    {
        this.internalMap = internalMap;
        this.numericComparator = numericComparator;
        this.queryType = queryType;
    }

    @Override
    public List<T> isGreaterOrEqualThan(S startValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element -> numericComparator.compareToValue(element, startValue) >= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isGreaterThan(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element -> numericComparator.compareToValue(element, value) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getRangeClosed(S startValue, S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element -> numericComparator.compareToValue(element, startValue) >= 0)
                .filter(element -> numericComparator.compareToValue(element, endValue) <= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getRange(S startValue, S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element -> numericComparator.compareToValue(element, startValue) >= 0)
                .filter(element -> numericComparator.compareToValue(element, endValue) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isLessOrEqualThan(S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element -> numericComparator.compareToValue(element, endValue) <= 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isLessThan(S endValue)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element -> numericComparator.compareToValue(element, endValue) < 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAscending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(numericComparator::compareToAggregate)
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAscending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted(numericComparator::compareToAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getDescending(int amount)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> numericComparator.compareToAggregate(element2, element1))
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getDescending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> numericComparator.compareToAggregate(element2, element1))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element-> numericComparator.compareToValue(element, value) == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> isNotEqualTo(S value)
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .filter(element-> numericComparator.compareToValue(element, value) != 0)
                .collect(Collectors.toList());
    }

}
