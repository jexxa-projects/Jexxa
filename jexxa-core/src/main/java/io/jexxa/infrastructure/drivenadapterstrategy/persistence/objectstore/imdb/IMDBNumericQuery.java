package io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.imdb;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.comparator.NumericComparator;
import io.jexxa.infrastructure.drivenadapterstrategy.persistence.objectstore.INumericQuery;

class IMDBNumericQuery<T, K, S> implements INumericQuery<T, S>
{
    NumericComparator<T, S> numericComparator;
    Map<K, T> internalMap;

    private Map<K, T> getOwnAggregateMap()
    {
        return internalMap;
    }

    IMDBNumericQuery(Map<K, T> internalMap, NumericComparator<T, S> numericComparator)
    {
        this.internalMap = internalMap;
        this.numericComparator = numericComparator;
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
                .sorted((element1, element2) -> numericComparator.compareToAggregate(element1, element2))
                .limit(amount)
                .collect(Collectors.toList());
    }

    @Override
    public List<T> getAscending()
    {
        return getOwnAggregateMap()
                .values()
                .stream()
                .sorted((element1, element2) -> numericComparator.compareToAggregate(element1, element2))
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

}
